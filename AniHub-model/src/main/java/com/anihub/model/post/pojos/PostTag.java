package com.anihub.model.post.pojos;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/*    post_id BIGINT NOT NULL COMMENT '帖子ID',
            tag_id BIGINT NOT NULL COMMENT '标签ID',*/
@Data
public class PostTag {
    @ApiModelProperty("帖子ID")
    private Long postId;
    @ApiModelProperty("标签ID")
    private Long tagId;
}
