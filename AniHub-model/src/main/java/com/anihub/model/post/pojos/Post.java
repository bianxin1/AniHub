package com.anihub.model.post.pojos;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/*    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键，自动递增',
            user_id BIGINT NOT NULL COMMENT '用户ID，发布者',
            layout_id BIGINT NOT NULL COMMENT '版面ID，所属版面',
            title VARCHAR(100) NOT NULL COMMENT '帖子标题',
            comment_count BIGINT NOT NULL DEFAULT 0 COMMENT '总回复数',
            is_delete INT DEFAULT 0 COMMENT '0:未删除，1：已删除',
            created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
            updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'*/
@Data
public class Post {
    @ApiModelProperty("帖子ID")
    private Long id;
    @ApiModelProperty("用户ID")
    private Long userId;
    @ApiModelProperty("版面ID")
    private Long layoutId;
    @ApiModelProperty("帖子标题")
    private String title;
    @ApiModelProperty("总回复数")
    private Long commentCount;
    @ApiModelProperty("状态")
    private Integer status;
    @ApiModelProperty("创建时间")
    private Date createdAt;
    @ApiModelProperty("更新时间")
    private Date updatedAt;
    @ApiModelProperty("热点值")
    private Integer hotValue;
}
