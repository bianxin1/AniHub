package com.anihub.coment.listener;

import com.anihub.coment.service.ICommentService;
import com.anihub.model.comment.pojo.CommentLike;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.boot.context.properties.ConstructorBinding;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CommentCountListener {
    private final ICommentService commentService;
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = "comment.like.queue", durable = "true"),
            exchange = @Exchange(name = "comment.direct"),
            key = "comment.like"
    ))

    public void updateCommentCount(CommentLike commentLike) {
        commentService.saveLike(commentLike);
    }
}
