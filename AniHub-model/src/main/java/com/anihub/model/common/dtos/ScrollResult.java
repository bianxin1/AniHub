package com.anihub.model.common.dtos;

import lombok.Data;

import java.util.List;

@Data
public class ScrollResult {
    private List<?> list;
    private Long minTime;
    private Integer offset;
}
