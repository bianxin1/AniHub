package com.anihub.post.controller;

import com.anihub.model.common.dtos.Result;
import com.anihub.model.post.dtos.PostRedisDto;
import com.anihub.post.service.IPostCollectService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(tags = "帖子收藏")
@RestController
@RequestMapping("/posts/collect")
@RequiredArgsConstructor
@Slf4j
public class PostCollectController {
    private final IPostCollectService collectService;
    @ApiOperation("收藏帖子")
    @PostMapping("/save")
    public Result post(@RequestParam("postId") Long postId, @RequestParam("folderId") Integer folderId) {
        log.info("收藏帖子");
        collectService.savePost(postId,folderId);
        return Result.success();
    }
    @ApiOperation("查询收藏")
    @GetMapping("/list")
    public Result<List<PostRedisDto>> listPost(@RequestParam("folderId") Integer folderId) {
        log.info("查询收藏");
        List<PostRedisDto> postRedisDtos = collectService.listPost(folderId);
        return Result.success(postRedisDtos);
    }
}
