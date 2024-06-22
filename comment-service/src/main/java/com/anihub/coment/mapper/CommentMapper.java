package com.anihub.coment.mapper;

import com.anihub.model.comment.pojo.Comment;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Select;

import java.util.Map;

public interface CommentMapper extends BaseMapper<Comment> {
    @Select("SELECT user_id, created_at FROM comment WHERE post_id = #{postId} AND floor = (SELECT MAX(floor) FROM comment WHERE post_id = #{postId})")
    Map<String, Object> getByLastId(Long postId);
}
