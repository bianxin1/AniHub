package com.anihub.post.controller;

import com.anihub.model.common.dtos.PageResult;
import com.anihub.model.common.dtos.Result;
import com.anihub.model.common.dtos.ScrollResult;
import com.anihub.model.post.dtos.PostDto;
import com.anihub.model.post.dtos.PostQueryDto;
import com.anihub.post.service.IPostService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Api(tags = "帖子管理")
@RestController
@RequestMapping("/posts")
@Slf4j
@RequiredArgsConstructor

public class PostController {
    private final IPostService postService;

    @ApiOperation("新增帖子")
    @PostMapping("/add")
    public Result add(@RequestBody PostDto postDto) {
        postService.add(postDto);
        return Result.success();
    }
    @ApiOperation("滚动查询帖子")
    @GetMapping("/scroll")
    public Result<ScrollResult> scroll(
            @RequestParam("layoutId") Long layoutId,@RequestParam("lastId") Long max, @RequestParam(value = "offset", defaultValue = "0") Integer offset) {
        return Result.success(postService.scroll(layoutId,max, offset));
    }


}
