package com.anihub.post.mapper;

import com.anihub.model.post.pojos.Post;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Update;

public interface PostMapper extends BaseMapper<Post> {
    @Update("update post set like_count =#{i} where id = #{postId}")
    void updateLikeCount(Long postId, int i);
}
