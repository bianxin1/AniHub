package com.anihub.model.layout.pojos;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/*    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键，自动递增',
            name VARCHAR(16) NOT NULL COMMENT '标签名称',
                layout_id BIGINT NOT NULL COMMENT '版面ID',

            description TEXT DEFAULT NULL COMMENT '标签描述'*/
@Data
public class Tag {
    @ApiModelProperty("标签ID")
    private Long id;
    @ApiModelProperty("标签名称")
    private String name;
    @ApiModelProperty("版面ID")
    private Long layoutId;
    @ApiModelProperty("标签描述")
    private String description;
}
