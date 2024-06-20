package com.anihub.model.post.pojos;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/*    post_id BIGINT PRIMARY KEY COMMENT '帖子ID',
            content TEXT NOT NULL COMMENT '帖子内容',*/
@Data

public class PostContent {
    @ApiModelProperty("帖子ID")
    private Long postId;
    @ApiModelProperty("帖子内容")
    private String content;
}
