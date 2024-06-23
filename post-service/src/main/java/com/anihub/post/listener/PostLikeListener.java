package com.anihub.post.listener;

import com.anihub.model.post.pojos.PostLike;
import com.anihub.post.service.IPostService;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PostLikeListener {
    private final IPostService postService;
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = "post.like.queue", durable = "true"),
            exchange = @Exchange(name = "post.direct"),
            key = "post.like"
    ))
    public void listenLike(PostLike postLike) {
        postService.saveLike(postLike);
    }
}
