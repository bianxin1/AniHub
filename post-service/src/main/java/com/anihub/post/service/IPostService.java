package com.anihub.post.service;

import com.anihub.model.common.dtos.PageResult;
import com.anihub.model.post.dtos.PostDto;
import com.anihub.model.post.dtos.PostQueryDto;
import com.anihub.model.post.pojos.Post;
import com.baomidou.mybatisplus.extension.service.IService;

public interface IPostService extends IService<Post> {
    /**
     * 添加帖子
     * @param postDto
     */
    void add(PostDto postDto);

    /**
     * 分页查询帖子
     * @param postQueryDto
     * @return
     */
    //PageResult query(PostQueryDto postQueryDto);
}
