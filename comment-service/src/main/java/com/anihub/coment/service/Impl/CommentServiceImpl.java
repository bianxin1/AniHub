package com.anihub.coment.service.Impl;

import cn.hutool.core.bean.BeanUtil;
import com.anihub.coment.client.UserClient;
import com.anihub.coment.mapper.CommentLikeMapper;
import com.anihub.coment.mapper.CommentMapper;
import com.anihub.coment.service.ICommentService;
import com.anihub.common.utils.UserContext;
import com.anihub.model.comment.dtos.CommentDto;
import com.anihub.model.comment.dtos.PageCommentDto;
import com.anihub.model.comment.pojo.Comment;
import com.anihub.model.comment.pojo.CommentLike;
import com.anihub.model.comment.vo.CommentVo;
import com.anihub.model.common.dtos.PageResult;
import com.anihub.model.post.pojos.PostLike;
import com.anihub.model.user.pojos.User;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CommentServiceImpl extends ServiceImpl<CommentMapper, Comment> implements ICommentService {
    private final CommentMapper commentMapper;
    private final StringRedisTemplate stringRedisTemplate;
    private final RabbitTemplate rabbitTemplate;
    private final CommentLikeMapper commentLikeMapper;
    private final UserClient userClient;
    private final static String COMMENT_LIKE_KEY = "comment:like:";
    private final static String COMMENT_DISLIKE_KEY = "comment:dislike:";
    private final static String COMMENT_LIKE_COUNT_KEY = "comment:like:count:";

    @Override
    public void save(CommentDto commentDto) {
        Comment comment = new Comment();
        BeanUtils.copyProperties(commentDto, comment);
        comment.setUserId(UserContext.getUser());
        Long floor = stringRedisTemplate.opsForValue().increment("post:comment:" + comment.getPostId(), 1);
        comment.setFloor(floor);
        int insert = commentMapper.insert(comment);
        stringRedisTemplate.opsForValue().set(COMMENT_LIKE_COUNT_KEY + comment.getId(), "0");
        if (insert > 0) {
            try {
                rabbitTemplate.convertAndSend("post.direct", "post.add.comment", comment.getPostId());
            } catch (Exception e) {
                log.error("发送消息失败", e);
            }
        }
    }

    @Override
    public Map<String, Object> getByLastId(Long postId) {
        return commentMapper.getByLastId(postId);
    }

    /**
     * 点赞或者点踩评论
     */
    @Override
    public void like(Long commentId, Short type) {
        // 1. 构建 Redis key
        String likeKey = COMMENT_LIKE_KEY + commentId;
        String dislikeKey = COMMENT_DISLIKE_KEY + commentId;
        String postLikeCountKey = COMMENT_LIKE_COUNT_KEY + commentId;

        // 2. 获取当前用户 ID
        Long userId = UserContext.getUser();
        String userIdStr = userId.toString();

        try {
            if (type == 1) {
                handleLike(likeKey, dislikeKey, postLikeCountKey, userIdStr);
            } else if (type == -1) {
                handleDislike(likeKey, dislikeKey, postLikeCountKey, userIdStr);
            } else if (type == 0) {
                stringRedisTemplate.opsForSet().remove(likeKey, userIdStr);
                stringRedisTemplate.opsForSet().remove(dislikeKey, userIdStr);
            } else {
                throw new IllegalArgumentException("Invalid type: " + type);
            }

            // 3. 发送消息
            sendLikeMessage(commentId, userId, type);
        } catch (Exception e) {
            log.error("Failed to like post", e);
            throw new RuntimeException("Failed to like post", e);
        }
    }

    public void saveLike(CommentLike commentLike) {
        //查找数据库
        QueryWrapper<CommentLike> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("comment_id", commentLike.getCommentId()).eq("user_id", commentLike.getUserId());
        CommentLike pk = commentLikeMapper.selectOne(queryWrapper);
        if (pk == null) {
            commentLikeMapper.insert(commentLike);
        } else {
            //更新
            commentLikeMapper.update(commentLike, queryWrapper);
            String s = stringRedisTemplate.opsForValue().get(COMMENT_LIKE_COUNT_KEY + commentLike.getCommentId());
            if (s != null) {
                commentLikeMapper.updateLikeCount(commentLike.getCommentId(), Integer.parseInt(s));
            }
        }
    }

    /**
     * 分页获取评论详情
     *
     * @param pageCommentDto
     * @return
     */
    @Override
    public PageResult show(PageCommentDto pageCommentDto) {
        // 创建分页对象并设置排序
        Page<Comment> page = Page.of(pageCommentDto.getPage(), pageCommentDto.getPageSize());
        page.addOrder(new OrderItem("floor", true));

        // 构建查询条件
        QueryWrapper<Comment> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("post_id", pageCommentDto.getPostId());

        // 执行分页查询
        Page<Comment> commentPage = commentMapper.selectPage(page, queryWrapper);

        // 获取评论记录
        List<Comment> records = commentPage.getRecords();

        // 将评论记录转换为 CommentVo 列表
        List<CommentVo> commentVos = BeanUtil.copyToList(records, CommentVo.class);

        // 获取所有评论的用户ID列表
        List<Long> userIds = commentVos.stream()
                .map(CommentVo::getUserId)
                .distinct()
                .collect(Collectors.toList());

        // 批量查询用户信息
        List<User> users = userClient.selectBatch(userIds);

        // 将用户信息映射为 Map，便于快速查找
        Map<Long, User> userMap = users.stream()
                .collect(Collectors.toMap(User::getId, user -> user));

        // 填充用户信息到 CommentVo 中
        commentVos.forEach(commentVo -> {
            User user = userMap.get(commentVo.getUserId());
            if (user != null) {
                commentVo.setUserName(user.getUsername());
                commentVo.setAvatar(user.getAvatar());
            }
        });

        // 封装返回结果
        PageResult pageResult = new PageResult();
        pageResult.setTotal(commentPage.getTotal());
        pageResult.setRecords(commentVos);
        return pageResult;
    }


    private void handleLike(String likeKey, String dislikeKey, String postLikeCountKet, String userId) {
        if (Boolean.TRUE.equals(stringRedisTemplate.opsForSet().isMember(likeKey, userId))) {
            stringRedisTemplate.opsForSet().remove(likeKey, userId);
            stringRedisTemplate.opsForValue().decrement(postLikeCountKet);
        } else {
            stringRedisTemplate.opsForSet().add(likeKey, userId);
            stringRedisTemplate.opsForValue().increment(postLikeCountKet);
            if (Boolean.TRUE.equals(stringRedisTemplate.opsForSet().isMember(dislikeKey, userId))) {
                stringRedisTemplate.opsForSet().remove(dislikeKey, userId);
                stringRedisTemplate.opsForValue().increment(postLikeCountKet);
            }
        }
    }

    private void handleDislike(String likeKey, String dislikeKey, String postLikeCountKet, String userId) {
        if (Boolean.TRUE.equals(stringRedisTemplate.opsForSet().isMember(dislikeKey, userId))) {
            stringRedisTemplate.opsForSet().remove(dislikeKey, userId);
            stringRedisTemplate.opsForValue().increment(postLikeCountKet);
        } else {
            stringRedisTemplate.opsForSet().add(dislikeKey, userId);
            stringRedisTemplate.opsForValue().decrement(postLikeCountKet);
            if (Boolean.TRUE.equals(stringRedisTemplate.opsForSet().isMember(likeKey, userId))) {
                stringRedisTemplate.opsForSet().remove(likeKey, userId);
                stringRedisTemplate.opsForValue().decrement(postLikeCountKet);
            }
        }
    }

    private void sendLikeMessage(Long commentId, Long userId, Short type) {
        CommentLike commentLike = new CommentLike();
        commentLike.setCommentId(commentId);
        commentLike.setUserId(userId);
        commentLike.setStatus(type);
        rabbitTemplate.convertAndSend("comment.direct", "comment.like", commentLike);
    }
}
