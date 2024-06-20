package com.anihub.post.client;

import com.anihub.model.layout.pojos.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "layout-service")
public interface LayoutClient {
    @GetMapping("/tags/selectByids")
    List<Tag> selectByids(@RequestParam("ids") List<Long> ids);
}
