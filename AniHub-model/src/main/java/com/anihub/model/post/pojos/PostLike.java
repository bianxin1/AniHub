package com.anihub.model.post.pojos;

import lombok.Data;

@Data
public class PostLike {
        private Long postId;
        private Long userId;
        private Short status;
}
