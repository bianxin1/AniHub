package com.anihub.model.post.dtos;

import lombok.Data;

@Data
public class PostQueryDto {
    private int page;
    private int pageSize;
    private Long layoutId;

}
