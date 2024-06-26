package com.anihub.post.job;

import com.anihub.post.service.IPostService;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JobHandler {
    private final IPostService postService;

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
    }
}