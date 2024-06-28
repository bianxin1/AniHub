package com.anihub.post.service;

import com.anihub.model.common.dtos.PageResult;
import com.anihub.model.common.dtos.ScrollResult;
import com.anihub.model.post.dtos.PostDto;
import com.anihub.model.post.dtos.PostQueryDto;
import com.anihub.model.post.dtos.PostRedisDto;
import com.anihub.model.post.pojos.Post;
import com.anihub.model.post.pojos.PostLike;
import com.anihub.model.post.vo.PostVo;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.Map;

public interface IPostService extends IService<Post> {
    /**
     * 添加帖子
     *
     * @param postDto
     */
    void add(PostDto postDto);

    /**
     * 滚动查询帖子
     *
     * @param max
     * @param offset
     * @return
     */
    ScrollResult scroll(Long layoutId, Long max, Integer offset);

    /**
     * 点赞或者点踩帖子
     *
     * @param postId
     * @param type
     */
    void like(Long postId, Short type);

    void saveLike(PostLike postLike);


    PostRedisDto findPostById(Long postId);

    /**
     * 获取帖子详情
     * @param postId
     * @return
     */
    PostVo findById(Long postId);

    /**
     * 增加帖子浏览数
     * @param postId
     */
    void incViewCount(Long postId);

    /**
     * 更新帖子点赞数
     */
    void updatePostViewCount();

    /**
     * 更新帖子热点值
     */
    void updatePostHot();

    /**
     * 更新读取热点帖子到redis
     */
    void updateHotPostToRedis(Long parentId);

    /**
     * 获取热点帖子列表
     * @return
     */
    List<PostRedisDto> hot(Long layoutId);
}