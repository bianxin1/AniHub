package com.anihub.model.post.pojos;

import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;
import lombok.Data;

import java.util.Date;

@Data
public class CollectPost {
    @ApiModelProperty("收藏夹id")
    private Integer folderId;
    @ApiModelProperty("帖子id")
    private Long postId;
    @ApiModelProperty("收藏时间")
    private Date favorite_at;
}
