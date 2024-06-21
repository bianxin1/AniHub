package com.anihub.coment.service;

import com.anihub.model.comment.dtos.CommentDto;
import com.anihub.model.comment.pojo.Comment;
import com.baomidou.mybatisplus.extension.service.IService;

public interface ICommentService extends IService<Comment> {
    /**
     * 保存评论
     * @param commentDto
     */
    void save(CommentDto commentDto);
}
