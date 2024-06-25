package com.anihub.post.service;

import com.anihub.model.post.dtos.PostRedisDto;
import com.anihub.model.post.pojos.CollectPost;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface IPostCollectService extends IService<CollectPost> {
    /**
     * 保存收藏帖子
     * @param postId
     * @param folderId
     */
    void savePost(Long postId, Integer folderId);

    /**
     * 获取收藏夹列表
     * @param folderId
     * @return
     */
    List<PostRedisDto> listPost(Integer folderId);
}
