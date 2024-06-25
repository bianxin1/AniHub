package com.anihub.coment.client;

import com.anihub.model.user.pojos.User;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "user-service")
public interface UserClient {
    @GetMapping("/users/select")
    User select(@RequestParam("id") Long id);
    @PostMapping("/users/selectBatch")
    List<User> selectBatch(@RequestBody List<Long> userIds);
}
