package com.anihub.layout.controller;

import com.anihub.layout.service.ILayoutService;
import com.anihub.model.common.dtos.Result;
import com.anihub.model.layout.dtos.LayoutDto;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(tags = "版面管理")
@RestController
@RequiredArgsConstructor
@RequestMapping("/layouts")
public class LayoutController {
    private final ILayoutService layoutService;

    @ApiOperation("新增版面")
    @PostMapping("/add")
    public Result add(@RequestBody LayoutDto layoutDto) {
        layoutService.add(layoutDto);
        return Result.success();
    }

    @ApiOperation("展示父版面")
    @GetMapping("/list")
    public Result list() {
        return Result.success(layoutService.list());
    }

    @GetMapping("/getAllParentId")
    List<Long> getAllParentId() {
        return layoutService.getAllParentId();
    }

    ;

    @GetMapping("/getLayoutIdByParentId")
    List<Long> getLayoutIdByParentId(@RequestParam("parentId") Long parentId) {
        return layoutService.getLayoutIdByParentId(parentId);
    }
}

