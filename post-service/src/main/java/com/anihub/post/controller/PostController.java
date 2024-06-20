package com.anihub.post.controller;

import com.anihub.model.common.dtos.PageResult;
import com.anihub.model.common.dtos.Result;
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

    /*@ApiOperation("分页查询帖子")
    @GetMapping("/query")
    public Result<PageResult> query(@RequestBody PostQueryDto postQueryDto) {
        PageResult pageResult = postService.query(postQueryDto);
        return Result.success(pageResult);
    }*/


}
