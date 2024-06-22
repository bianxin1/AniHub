package com.anihub.coment.service;

import com.anihub.model.comment.dtos.CommentDto;
import com.anihub.model.comment.pojo.Comment;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.Map;

public interface ICommentService extends IService<Comment> {
    /**
     * 保存评论
     * @param commentDto
     */
    void save(CommentDto commentDto);

    Map<String, Object> getByLastId(Long postId);
}
