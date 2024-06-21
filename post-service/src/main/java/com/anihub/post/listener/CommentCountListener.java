package com.anihub.post.listener;

import com.anihub.model.post.pojos.Post;
import com.anihub.post.service.IPostService;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CommentCountListener {
    private final IPostService postService;

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = "post.add.comment.queue", durable = "true"),
            exchange = @Exchange(name = "post.direct"),
            key = "post.add.comment"
    ))
    public void listenAddComment(Long postId) {
        Post post = postService.getById(postId);
        post.setCommentCount(post.getCommentCount() + 1);
        postService.updateById(post);
    }
}
