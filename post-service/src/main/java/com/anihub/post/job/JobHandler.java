package com.anihub.post.job;

import com.anihub.post.client.LayoutClient;
import com.anihub.post.service.IPostService;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class JobHandler {
    private final IPostService postService;
    private final LayoutClient layoutClient;
    /**
     * 更新帖子浏览数
     */
    @XxlJob("updatePostViewCount")
    public void updatePostViewCount() {
        postService.updatePostViewCount();
    }
    /**
     * 更新帖子热点值
     */
    @XxlJob("updatePostHot")
    public void updatePostHot() {
        postService.updatePostHot();
        List<Long> allParentId = layoutClient.getAllParentId();
        log.info("allParentId:{}",allParentId);
        allParentId.parallelStream().forEach(postService::updateHotPostToRedis);
    }
}