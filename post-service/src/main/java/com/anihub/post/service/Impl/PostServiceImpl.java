package com.anihub.post.service.Impl;

import com.anihub.common.utils.UserContext;
import com.anihub.model.common.dtos.Result;
import com.anihub.model.common.dtos.ScrollResult;
import com.anihub.model.layout.pojos.Tag;
import com.anihub.model.post.dtos.PostDto;
import com.anihub.model.post.dtos.PostRedisDto;
import com.anihub.model.post.pojos.Post;
import com.anihub.model.post.pojos.PostContent;
import com.anihub.model.post.pojos.PostTag;
import com.anihub.model.user.pojos.User;
import com.anihub.post.client.CommentClient;
import com.anihub.post.client.LayoutClient;
import com.anihub.post.client.UserClient;
import com.anihub.post.mapper.PostContentMapper;
import com.anihub.post.mapper.PostMapper;
import com.anihub.post.mapper.PostTagMapper;
import com.anihub.post.service.IPostService;
import com.anihub.post.utils.CacheClient;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostServiceImpl extends ServiceImpl<PostMapper, Post> implements IPostService{
    private final PostMapper postMapper;
    private final PostContentMapper postContentMapper;
    private final PostTagMapper postTagMapper;
    private final StringRedisTemplate stringRedisTemplate;
    private final CacheClient cacheClient;
    private final UserClient userClient;
    private final LayoutClient layoutClient;
    private final CommentClient commentClient;
    /**
     * 添加帖子
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
     * @param max
     * @param offset
     * @return
     */
    @Override
    public ScrollResult scroll(Long layoutId,Long max, Integer offset) {
        String key = "post:time:" + layoutId;
        Set<ZSetOperations.TypedTuple<String>> typedTuples = stringRedisTemplate.opsForZSet().reverseRangeByScoreWithScores(key, 0, max, offset, 10);
        //非空判断
        if (typedTuples==null||typedTuples.isEmpty()) {
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
            if(time == minTime){
                os++;
            }else{
                minTime = time;
                os = 1;
            }
        }
        os = minTime == max ? os : os + offset;
        // 5.查询帖子
        List<PostRedisDto> postRedisDtos = new ArrayList<>();
        Function<Long, PostRedisDto> dbFallback = this::findPostById;
        for (Long id : ids) {
            PostRedisDto postRedisDto = cacheClient.queryWithMutex("post:info:",id, PostRedisDto.class,dbFallback,1L, TimeUnit.DAYS);
            postRedisDtos.add(postRedisDto);
        }
        // 6.返回结果
        ScrollResult scrollResult = new ScrollResult();
        scrollResult.setOffset(os);
        scrollResult.setList(postRedisDtos);
        scrollResult.setMinTime(minTime);
        return scrollResult;
    }

    private PostRedisDto findPostById(Long postId) {
        PostRedisDto postRedisDto = new PostRedisDto();
        Post post = postMapper.selectById(postId);
        BeanUtils.copyProperties(post, postRedisDto);
        User userInfo = userClient.select(post.getUserId());
        postRedisDto.setUsername(userInfo.getUsername());
        // 使用mp查询标签，使用query构建查询条件
        QueryWrapper<PostTag> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("tag_id").eq("post_id", postId);
        List<Long> tags = postTagMapper.selectObjs(queryWrapper).stream()
                .map(obj -> (Long) obj)
                .collect(Collectors.toList());
        List<Tag> tagNames = layoutClient.selectByids(tags);
        List<String> tagNamesList = tagNames.stream().map(Tag::getName).toList();
        postRedisDto.setTagName(tagNamesList);
        postRedisDto.setTags(tags);
        Map<String, Object> byLastId = commentClient.getByLastId(postId);
        if (byLastId == null) {
            return postRedisDto;
        }
        postRedisDto.setLastUserId((Long) byLastId.get("user_id"));
        postRedisDto.setLastUsername(userClient.select(postRedisDto.getLastUserId()).getUsername());
        //TODO date填充
        return postRedisDto;
    }


}
