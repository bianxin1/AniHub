package com.anihub.model.layout.pojos;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/*    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键，自动递增',
            name VARCHAR(100) NOT NULL COMMENT '版面名称',
            description TEXT DEFAULT NULL COMMENT '版面描述',
            parent_id BIGINT DEFAULT NULL COMMENT '父版面ID',
            created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
            updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
            CONSTRAINT fk_parent FOREIGN KEY (parent_id) REFERENCES layout(id)*/
@Data
public class Layout {
    @ApiModelProperty("版面ID")
    private Long id;
    @ApiModelProperty("版面名称")
    private String name;
    @ApiModelProperty("版面描述")
    private String description;
    @ApiModelProperty("父版面ID")
    private Long parentId;
    @ApiModelProperty("创建时间")
    private Date createdAt;
    @ApiModelProperty("更新时间")
    private Date updatedAt;
}
