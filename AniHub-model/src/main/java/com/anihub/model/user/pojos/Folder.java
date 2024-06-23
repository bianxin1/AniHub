package com.anihub.model.user.pojos;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/*id INT AUTO_INCREMENT PRIMARY KEY COMMENT '主键，自动递增',
        user_id BIGINT NOT NULL COMMENT '用户ID',
        name VARCHAR(32) NOT NULL COMMENT '收藏夹名称',
        status INT DEFAULT 0 COMMENT '0:私密，1：公开，2：删除',
        created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
        updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'*/
@Data
public class Folder {
    @ApiModelProperty("主键，自动递增")
    private Integer id;
    @ApiModelProperty("用户ID")
    private Long userId;
    @ApiModelProperty("收藏夹名称")
    private String name;
    @ApiModelProperty("内容数量")
    private Long count;
    @ApiModelProperty("0:私密，1：公开，2：删除")
    private Integer status;
    @ApiModelProperty("创建时间")
    private Date createdAt;
    @ApiModelProperty("更新时间")
    private Date updatedAt;
}
