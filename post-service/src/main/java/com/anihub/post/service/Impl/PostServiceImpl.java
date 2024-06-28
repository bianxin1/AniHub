package com.anihub.post.service.Impl;


import com.anihub.common.utils.UserContext;
import com.anihub.model.common.dtos.ScrollResult;
import com.anihub.model.layout.pojos.Tag;
import com.anihub.model.post.dtos.PostDto;
import com.anihub.model.post.dtos.PostRedisDto;
import com.anihub.model.post.pojos.Post;
import com.anihub.model.post.pojos.PostContent;
import com.anihub.model.post.pojos.PostLike;
import com.anihub.model.post.pojos.PostTag;
import com.anihub.model.post.vo.PostVo;
import com.anihub.model.user.pojos.User;
import com.anihub.post.client.CommentClient;
import com.anihub.post.client.LayoutClient;
import com.anihub.post.client.UserClient;
import com.anihub.post.mapper.PostContentMapper;
import com.anihub.post.mapper.PostLikeMapper;
import com.anihub.post.mapper.PostMapper;
import com.anihub.post.mapper.PostTagMapper;
import com.anihub.post.service.IPostService;
import com.anihub.post.utils.CacheClient;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.time.temporal.Temporal;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PostServiceImpl extends ServiceImpl<PostMapper, Post> implements IPostService {
    private final PostMapper postMapper;
    private final PostContentMapper postContentMapper;
    private final PostTagMapper postTagMapper;
    private final StringRedisTemplate stringRedisTemplate;
    private final CacheClient cacheClient;
    private final UserClient userClient;
    private final LayoutClient layoutClient;
    private final CommentClient commentClient;
    private final RabbitTemplate rabbitTemplate;
    private final PostLikeMapper postLikeMapper;

    /**
     * 添加帖子
     *
     * @param postDto
     */
    @Override
    @Transactional
    public void add(PostDto postDto) {
        try {
            Post post = new Post();
            BeanUtils.copyProperties(postDto, post);
            post.setUserId(UserContext.getUser());
            // 保存帖子
            postMapper.insert(post);
            // 保存帖子内容
            PostContent postContent = new PostContent();
            postContent.setContent(postDto.getContent());
            postContent.setPostId(post.getId());
            postContentMapper.insert(postContent);
            // 封装标签集合
            List<Long> tags = postDto.getTags();
            if (!tags.isEmpty()) {
                // 保存帖子标签
                Map<String, Object> params = new HashMap<>();
                params.put("id", post.getId());
                params.put("tags", tags);
                postTagMapper.insertBatch(params);
            }
            // 缓存到 Redis
            boolean redisSuccess = false;
            try {
                stringRedisTemplate.opsForZSet().add("post:time:" + post.getLayoutId(), post.getId().toString(), System.currentTimeMillis());
                stringRedisTemplate.opsForValue().set("post:comment:" + post.getId(), "0");
                stringRedisTemplate.opsForValue().set("post:like:count:" + post.getId(), "0");
                redisSuccess = true;
                PostRedisDto postRedisDto = new PostRedisDto();
                BeanUtils.copyProperties(post, postRedisDto);
                postRedisDto.setLastUserId(post.getUserId());
                User userInfo = userClient.select(post.getUserId());
                postRedisDto.setUsername(userInfo.getUsername());
                postRedisDto.setLastUsername(userInfo.getUsername());
                postRedisDto.setCommentAt(post.getCreatedAt());
                List<Tag> tagNames = layoutClient.selectByids(tags);
                List<String> tagNamesList = tagNames.stream().map(Tag::getName).toList();
                postRedisDto.setTagName(tagNamesList);
                postRedisDto.setTags(tags);
                cacheClient.set("post:info:" + post.getId(), postRedisDto, 1L, TimeUnit.DAYS);
            } catch (Exception e) {
                if (redisSuccess) {
                    // 如果 Redis 添加成功后发生异常，进行补偿删除
                    stringRedisTemplate.opsForZSet().remove("post:time:" + post.getLayoutId(), post.getId().toString());
                }
                throw e;  // 重新抛出异常，以便事务管理器处理
            }
        } catch (Exception e) {
            // 错误处理
            log.error("Failed to add post", e);
            throw new RuntimeException("Failed to add post", e);
        }
    }

    /**
     * 滚动查询帖子
     *
     * @param max
     * @param offset
     * @return
     */
    @Override
    public ScrollResult scroll(Long layoutId, Long max, Integer offset) {
        String key = "post:time:" + layoutId;
        Set<ZSetOperations.TypedTuple<String>> typedTuples = stringRedisTemplate.opsForZSet().reverseRangeByScoreWithScores(key, 0, max, offset, 10);
        //非空判断
        if (typedTuples == null || typedTuples.isEmpty()) {
            return new ScrollResult();
        }
        List<Long> ids = new ArrayList<>(typedTuples.size());
        long minTime = 0;
        int os = 0;
        for (ZSetOperations.TypedTuple<String> tuple : typedTuples) {
            // 4.1.获取id
            ids.add(Long.valueOf(tuple.getValue()));
            // 4.2.获取分数(时间戳）
            long time = tuple.getScore().longValue();
            if (time == minTime) {
                os++;
            } else {
                minTime = time;
                os = 1;
            }
        }
        os = minTime == max ? os : os + offset;
        // 5.查询帖子
        List<PostRedisDto> postRedisDtos = new ArrayList<>();
        Function<Long, PostRedisDto> dbFallback = this::findPostById;
        for (Long id : ids) {
            PostRedisDto postRedisDto = cacheClient.queryWithMutex("post:info:", id, PostRedisDto.class, dbFallback, 1L, TimeUnit.DAYS);
            postRedisDtos.add(postRedisDto);
        }
        // 6.返回结果
        ScrollResult scrollResult = new ScrollResult();
        scrollResult.setOffset(os);
        scrollResult.setList(postRedisDtos);
        scrollResult.setMinTime(minTime);
        return scrollResult;
    }

    /**
     * 点赞或者点踩帖子
     *
     * @param postId
     * @param type
     */

    @Override
    public void like(Long postId, Short type) {
        // 1. 构建 Redis key
        String likeKey = "post:like:" + postId;
        String dislikeKey = "post:dislike:" + postId;
        String postLikeCountKey = "post:like:count:" + postId;

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
            sendLikeMessage(postId, userId, type);
        } catch (Exception e) {
            log.error("Failed to like post", e);
            throw new RuntimeException("Failed to like post", e);
        }
    }

    @Override
    public void saveLike(PostLike postLike) {
        //查找数据库
        QueryWrapper<PostLike> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("post_id", postLike.getPostId()).eq("user_id", postLike.getUserId());
        PostLike pk = postLikeMapper.selectOne(queryWrapper);
        if (pk == null) {
            postLikeMapper.insert(postLike);
        } else {
            //更新
            postLikeMapper.update(postLike, queryWrapper);
        }
        String s = stringRedisTemplate.opsForValue().get("post:like:count:" + postLike.getPostId());
        if (s != null) {
            postMapper.updateLikeCount(postLike.getPostId(), Integer.parseInt(s));
        }
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

    private void sendLikeMessage(Long postId, Long userId, Short type) {
        PostLike postLike = new PostLike();
        postLike.setPostId(postId);
        postLike.setUserId(userId);
        postLike.setStatus(type);
        rabbitTemplate.convertAndSend("post.direct", "post.like", postLike);
    }

    public PostRedisDto findPostById(Long postId) {
        PostRedisDto postRedisDto = new PostRedisDto();
        Post post = postMapper.selectById(postId);
        BeanUtils.copyProperties(post, postRedisDto);
        User userInfo = userClient.select(post.getUserId());
        postRedisDto.setUsername(userInfo.getUsername());
        // 使用mp查询标签，使用query构建查询条件
        QueryWrapper<PostTag> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("tag_id").eq("post_id", postId);
        List<Long> tags = postTagMapper.selectObjs(queryWrapper).stream().map(obj -> (Long) obj).collect(Collectors.toList());
        if (!tags.isEmpty()) {
            List<Tag> tagNames = layoutClient.selectByids(tags);
            List<String> tagNamesList = tagNames.stream().map(Tag::getName).toList();
            postRedisDto.setTagName(tagNamesList);
            postRedisDto.setTags(tags);
        }
        Map<String, Object> byLastId = commentClient.getByLastId(postId);
        if (byLastId == null) {
            return postRedisDto;
        }
        Integer userIdInt = (Integer) byLastId.get("user_id");
        Long userIdLong = userIdInt != null ? userIdInt.longValue() : null; // 注意处理 null 值的情况
        postRedisDto.setLastUserId(userIdLong);
        postRedisDto.setLastUsername(userClient.select(postRedisDto.getLastUserId()).getUsername());
        //TODO date填充
        return postRedisDto;
    }

    @Override
    public PostVo findById(Long postId) {
        Post post = postMapper.selectById(postId);
        PostVo postVo = new PostVo();
        BeanUtils.copyProperties(post, postVo);
        User userInfo = userClient.select(post.getUserId());
        postVo.setUsername(userInfo.getUsername());
        postVo.setGrade(userInfo.getGrade());
        postVo.setAvatar(userInfo.getAvatar());
        // 使用mp查询帖子内容
        QueryWrapper<PostContent> queryWrapper1 = new QueryWrapper<>();
        queryWrapper1.select("content").eq("post_id", postId);
        PostContent postContent = postContentMapper.selectOne(queryWrapper1);
        postVo.setContent(postContent.getContent());
        // 使用mp查询标签，使用query构建查询条件
        QueryWrapper<PostTag> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("tag_id").eq("post_id", postId);
        List<Long> tags = postTagMapper.selectObjs(queryWrapper).stream().map(obj -> (Long) obj).collect(Collectors.toList());
        if (!tags.isEmpty()) {
            postVo.setTags(tags);
            List<Tag> tagNames = layoutClient.selectByids(tags);
            List<String> tagNamesList = tagNames.stream().map(Tag::getName).toList();
            postVo.setTagNames(tagNamesList);
        }
        return postVo;
    }

    /**
     * 增加帖子浏览数
     *
     * @param postId
     */
    @Override
    public void incViewCount(Long postId) {
        String key = "post:view:" + postId;
        stringRedisTemplate.opsForValue().increment(key);
    }

    /**
     * 获取帖子浏览数
     *
     * @param postId
     * @return
     */

    private Long getViewCount(Long postId) {
        String key = "post:view:" + postId;
        String value = stringRedisTemplate.opsForValue().get(key);
        return value != null ? Long.parseLong(value) : 0;
    }

    /**
     * 获取所有帖子ID
     *
     * @return
     */

    private List<Long> getAllPostId() {
        return postMapper.selectAllPostId();
    }

    /**
     * 更新帖子浏览数到mysql
     *
     * @param viewCounts
     */

    private void updateViewCounts(Map<Long, Long> viewCounts) {
        viewCounts.forEach((postId, viewCount) -> {
            postMapper.updateViewCount(postId, viewCount.intValue());
        });
    }

    @Override
    public void updatePostViewCount() {
        try {
            List<Long> postIds = getAllPostId();
            Map<Long, Long> viewCounts = postIds.parallelStream().collect(Collectors.toMap(postId -> postId, this::getViewCount));
            updateViewCounts(viewCounts);
        } catch (Exception e) {
            log.error("Failed to update post view count", e);
            throw new RuntimeException("Failed to update post view count", e);
        }

    }

    @Override
    @Transactional
    public void updatePostHot() {
        try {
            int pageSize = 100;
            int currentPage = 1;
            IPage<Post> postPage;

            // 获取固定的总记录数
            long totalRecords = postMapper.selectCount(null);
            long totalPages = (totalRecords + pageSize - 1) / pageSize;  // 计算总页数

            do {
                log.info("Updating post hot value, current page: {}", currentPage);
                Page<Post> page = new Page<>(currentPage, pageSize);
                postPage = postMapper.selectPage(page, null);

                // 确保分页信息正确
                log.info("Total pages: {}, Current page: {}, Total records: {}", totalPages, postPage.getCurrent(), totalRecords);

                if (postPage.getRecords().isEmpty()) {
                    log.info("No more posts to update, exiting loop");
                    break;
                }

                List<Post> updatedPosts = new ArrayList<>();
                postPage.getRecords().forEach(post -> {
                    double hotness = calculateHotness(post);
                    post.setHotValue(hotness);
                    updatedPosts.add(post);
                });

                updateBatchById(updatedPosts);
                currentPage++;
            } while (currentPage <= totalPages);
        } catch (Exception e) {
            log.error("Failed to update post hot value", e);
            throw new RuntimeException("Failed to update post hot value", e);
        }
    }

    /**
     * 更新热门帖子到 Redis
     */
    @Override
    public void updateHotPostToRedis(Long layoutId) {
        List<Long> childrenIds = layoutClient.getLayoutIdByParentId(layoutId);
        childrenIds.add(layoutId);
        List<Post> hotPosts = postMapper.selectList(new QueryWrapper<Post>().in("layout_id", childrenIds).orderByDesc("hot_value").last("limit 10"));
        String key = "layout:hot:" + layoutId;
        stringRedisTemplate.delete(key);
        // 使用批量操作
        Set<ZSetOperations.TypedTuple<String>> tuples = new HashSet<>();
        for (Post post : hotPosts) {
            tuples.add(ZSetOperations.TypedTuple.of(post.getId().toString(), post.getHotValue()));
        }
        stringRedisTemplate.opsForZSet().add(key, tuples);
        stringRedisTemplate.expire(key, 1, TimeUnit.DAYS);
    }

    /**
     * 获取热点帖子列表
     * @return
     */
    @Override
    public List<PostRedisDto> hot(Long layoutId) {
        String key = "layout:hot:" + layoutId;
        Set<ZSetOperations.TypedTuple<String>> tuples = stringRedisTemplate.opsForZSet().reverseRangeWithScores(key, 0, 9);
        if (tuples == null || tuples.isEmpty()) {
            return Collections.emptyList();
        }

        List<Long> postIds = tuples.stream()
                .map(t -> Long.parseLong(Objects.requireNonNull(t.getValue())))
                .toList();

        List<CompletableFuture<PostRedisDto>> futures = postIds.stream()
                .map(postId -> CompletableFuture.supplyAsync(() ->
                        cacheClient.queryWithMutex("post:info:", postId, PostRedisDto.class,
                                this::findPostById, 1L, TimeUnit.DAYS)))
                .toList();

        List<PostRedisDto> postRedisDtos = futures.stream()
                .map(CompletableFuture::join)
                .collect(Collectors.toList());

        return postRedisDtos;

    }

    private double calculateHotness(Post post) {
        long viewCount = post.getViewCount();
        long likeCount = post.getLikeCount();
        long commentCount = post.getCommentCount();

        // Convert Date to LocalDateTime
        LocalDateTime createdAt = Instant.ofEpochMilli(post.getCreatedAt().getTime()).atZone(ZoneId.systemDefault()).toLocalDateTime();
        long timeDifference = ChronoUnit.MINUTES.between(createdAt, LocalDateTime.now());

        return (double) (likeCount * 2 + commentCount * 4 + viewCount) / timeDifference;
    }
}
