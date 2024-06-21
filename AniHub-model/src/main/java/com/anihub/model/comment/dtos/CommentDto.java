package com.anihub.model.comment.dtos;

import lombok.Data;

@Data
public class CommentDto {
    private Long postId;
    private Long parentCommentId;
    private String content;
}
