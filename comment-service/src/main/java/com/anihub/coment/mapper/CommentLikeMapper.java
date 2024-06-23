package com.anihub.coment.mapper;

import com.anihub.model.comment.pojo.CommentLike;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Update;

public interface CommentLikeMapper extends BaseMapper<CommentLike> {
    @Update("update comment set like_count = #{i} where id = #{commentId}")
    void updateLikeCount(Long commentId, int i);
}
