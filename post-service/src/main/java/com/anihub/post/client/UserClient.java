package com.anihub.post.client;

import com.anihub.model.common.dtos.Result;
import com.anihub.model.user.pojos.User;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "user-service")
public interface UserClient {
    @GetMapping("/users/select")
    User select(@RequestParam("id") Long id);
}
