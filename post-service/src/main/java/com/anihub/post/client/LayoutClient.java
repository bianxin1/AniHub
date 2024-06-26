package com.anihub.post.client;

import com.anihub.model.layout.pojos.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "layout-service")
public interface LayoutClient {
    /**
     * 根据id批量查询标签
     * @param ids
     * @return
     */
    @GetMapping("/tags/selectByids")
    List<Tag> selectByids(@RequestParam("ids") List<Long> ids);

    /**
     * 获取所有父版面id
     * @return
     */
    @GetMapping("/layouts/getAllParentId")
    List<Long> getAllParentId();
    /**
     * 根据父版面id获取子版面id
     * @return
     */
    @GetMapping("/layouts/getLayoutIdByParentId")
    List<Long> getLayoutIdByParentId(@RequestParam("parentId") Long parentId);
}
