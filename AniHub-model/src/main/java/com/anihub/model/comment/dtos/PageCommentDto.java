package com.anihub.model.comment.dtos;

import lombok.Data;

@Data
public class PageCommentDto {
    private int page;

    private int pageSize;

    private Long postId;
}
