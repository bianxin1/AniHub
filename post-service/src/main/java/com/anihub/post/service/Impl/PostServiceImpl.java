package com.anihub.post.service.Impl;

import com.anihub.common.utils.UserContext;
import com.anihub.model.common.dtos.Result;
import com.anihub.model.layout.pojos.Tag;
import com.anihub.model.post.dtos.PostDto;
import com.anihub.model.post.dtos.PostRedisDto;
import com.anihub.model.post.pojos.Post;
import com.anihub.model.post.pojos.PostContent;
import com.anihub.model.user.pojos.User;
import com.anihub.post.client.LayoutClient;
import com.anihub.post.client.UserClient;
import com.anihub.post.mapper.PostContentMapper;
import com.anihub.post.mapper.PostMapper;
import com.anihub.post.mapper.PostTagMapper;
import com.anihub.post.service.IPostService;
import com.anihub.post.utils.CacheClient;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
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
                redisSuccess = true;

                PostRedisDto postRedisDto = new PostRedisDto();
                BeanUtils.copyProperties(post, postRedisDto);
                // TODO 填充其他数据，lastUserId，lastUsername，commentAt
                User userInfo = userClient.select(post.getUserId());
                postRedisDto.setUsername(userInfo.getUsername());
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



}
