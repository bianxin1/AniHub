package com.anihub.layout.controller;

import com.anihub.layout.service.ITagService;
import com.anihub.model.layout.pojos.Tag;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.apiguardian.api.API;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Api("标签管理")
@RestController
@RequestMapping("/tags")
@RequiredArgsConstructor
public class TagController {
    private final ITagService tagService;
    @ApiOperation("根据id批量查询标签")
    @GetMapping("/selectByids")
    public List<Tag> selectByids(@RequestParam("ids") List<Long> ids) {
        return tagService.listByIds(ids);
    }
}
