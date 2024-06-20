package com.anihub.model.layout.dtos;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class LayoutDto {
    @ApiModelProperty("版面名称")
    private String name;
    @ApiModelProperty("版面描述")
    private String description;
    @ApiModelProperty("父版面ID")
    private Long parentId;
}
