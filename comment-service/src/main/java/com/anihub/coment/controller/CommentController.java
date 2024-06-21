package com.anihub.coment.controller;

import com.anihub.coment.service.ICommentService;
import com.anihub.model.comment.dtos.CommentDto;
import com.anihub.model.common.dtos.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
@Api(tags = "评论管理")
@RestController
@RequestMapping("/comments")
@RequiredArgsConstructor
public class CommentController {
    private final ICommentService commentService;
    @ApiOperation("新增评论")
    @PostMapping("/save")
    public Result save(@RequestBody CommentDto commentDto) {
        commentService.save(commentDto);
        return Result.success();
    }



}
