package com.anihub.coment.service.Impl;

import com.anihub.coment.mapper.CommentMapper;
import com.anihub.coment.service.ICommentService;
import com.anihub.common.utils.UserContext;
import com.anihub.model.comment.dtos.CommentDto;
import com.anihub.model.comment.pojo.Comment;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class CommentServiceImpl extends ServiceImpl<CommentMapper, Comment> implements ICommentService {
    private final CommentMapper commentMapper;
    private final StringRedisTemplate stringRedisTemplate;
    private final RabbitTemplate rabbitTemplate;
    @Override
    public void save(CommentDto commentDto) {
        Comment comment = new Comment();
        BeanUtils.copyProperties(commentDto, comment);
        comment.setUserId(UserContext.getUser());
        Long floor = stringRedisTemplate.opsForValue().increment("post:comment:" + comment.getPostId(), 1);
        comment.setFloor(floor);
        int insert = commentMapper.insert(comment);
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
}
