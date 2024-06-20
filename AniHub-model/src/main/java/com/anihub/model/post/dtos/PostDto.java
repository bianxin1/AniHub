package com.anihub.model.post.dtos;

import com.anihub.model.post.pojos.PostTag;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class PostDto {
    @ApiModelProperty("版面ID")
    private Long layoutId;
    @ApiModelProperty("帖子标题")
    private String title;
    @ApiModelProperty("帖子内容")
    private String content;
    @ApiModelProperty("标签ID集合")
    private List<Long> tags = new ArrayList<>();
}
