package com.anihub.model.comment.pojo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Date;

/*    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键，自动递增',
            post_id BIGINT NOT NULL COMMENT '帖子ID，所属帖子',
            user_id BIGINT NOT NULL COMMENT '用户ID，回复者',
            parent_comment_id BIGINT DEFAULT NULL COMMENT '父回复ID，指向父回复',
            content TEXT NOT NULL COMMENT '回复内容',
            floor BIGINT NOT NULL COMMENT '楼层',
            status INT DEFAULT 0 COMMENT '0:未删除，1：已删除',
            created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
            updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'*/
@Data
public class Comment {
    @ApiModelProperty("主键，自动递增")
    private Long id;
    @ApiModelProperty("帖子ID，所属帖子")
    private Long postId;
    @ApiModelProperty("用户ID，回复者")
    private Long userId;
    @ApiModelProperty("父回复ID，指向父回复")
    private Long parentCommentId;
    @ApiModelProperty("回复内容")
    private String content;
    @ApiModelProperty("楼层")
    private Long floor;
    @ApiModelProperty("0:未删除，1：已删除")
    private Integer status;
    @ApiModelProperty("创建时间")
    private Date createdAt;
    @ApiModelProperty("更新时间")
    private Date updatedAt;
    @ApiModelProperty("点赞数")
    private Long likeCount;
}
