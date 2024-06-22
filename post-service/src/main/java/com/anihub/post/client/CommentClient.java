package com.anihub.post.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

@FeignClient(name = "comment-service")
public interface CommentClient {
    @GetMapping("/comments/getByLastId")
    Map<String, Object> getByLastId(@RequestParam("postId")Long postId);
}
