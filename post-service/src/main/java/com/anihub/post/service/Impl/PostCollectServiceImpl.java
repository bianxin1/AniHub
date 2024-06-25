package com.anihub.post.service.Impl;

import com.anihub.model.post.dtos.PostRedisDto;
import com.anihub.model.post.pojos.CollectPost;
import com.anihub.post.mapper.CollectPostMapper;
import com.anihub.post.service.IPostCollectService;
import com.anihub.post.service.IPostService;
import com.anihub.post.utils.CacheClient;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
public class PostCollectServiceImpl extends ServiceImpl<CollectPostMapper, CollectPost> implements IPostCollectService {
    private final CollectPostMapper collectPostMapper;
    private final CacheClient cacheClient;
    private final IPostService postService;
    /**
     * 保存收藏帖子
     * @param postId
     * @param folderId
     */
    @Override
    public void savePost(Long postId, Integer folderId) {
        CollectPost collectPost = new CollectPost();
        collectPost.setPostId(postId);
        collectPost.setFolderId(folderId);
        collectPostMapper.insert(collectPost);
    }

    /**
     * 获取收藏夹列表
     * @param folderId
     * @return
     */
    @Override
    public List<PostRedisDto> listPost(Integer folderId) {
        // 1.查询收藏夹下的帖子id
        QueryWrapper<CollectPost> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("folder_id", folderId);
        List<CollectPost> collectPosts = collectPostMapper.selectList(queryWrapper);
        List<Long> ids = collectPosts.stream().map(CollectPost::getPostId).toList();
        List<PostRedisDto> postRedisDtos = new ArrayList<>();
        Function<Long, PostRedisDto> dbFallback = postService::findPostById;
        // 2.查询帖子信息
        for (Long id : ids) {
            PostRedisDto postRedisDto = cacheClient.queryWithMutex("post:info:",id, PostRedisDto.class,dbFallback,1L, TimeUnit.DAYS);
            postRedisDtos.add(postRedisDto);
        }
        return postRedisDtos;
    }

}
