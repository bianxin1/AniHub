package com.anihub.model.comment.pojo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class CommentLike {
    @ApiModelProperty("用户id")
    private Long userId;
    @ApiModelProperty("评论id")
    private Long commentId;
    @ApiModelProperty("点赞状态:0:未点赞，1：已点赞")
    private Short status;
}
