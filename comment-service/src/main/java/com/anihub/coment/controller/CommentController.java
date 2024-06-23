package com.anihub.coment.controller;

import com.anihub.coment.service.ICommentService;
import com.anihub.model.comment.dtos.CommentDto;
import com.anihub.model.common.dtos.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

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
    @GetMapping("/getByLastId")
    public Map<String, Object> getByLastId(@RequestParam Long postId) {
        return commentService.getByLastId(postId);
    }
    @ApiOperation("点赞或者点踩评论")
    @PostMapping("/like")
    public Result like(@RequestParam("commentId") Long commentId, @RequestParam("type") Short type) {
        commentService.like(commentId, type);
        return Result.success();
    }



}
