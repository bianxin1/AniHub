package com.anihub.post.mapper;

import com.anihub.model.post.pojos.PostTag;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;
import java.util.Map;

public interface PostTagMapper extends BaseMapper<PostTag> {
    /**
     * 批量插入帖子标签
     */
    void insertBatch(Map<String, Object> params);


}
