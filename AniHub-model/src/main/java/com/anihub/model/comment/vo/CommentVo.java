package com.anihub.model.comment.vo;

import lombok.Data;

import java.util.Date;

@Data
public class CommentVo {
    private Long id;
    private Long userId;
    private String userName;
    private String avatar;
    private String content;
    private Long parentCommentId;
    private Long floor;
    private Date createdAt;
    private Long likeCount;
}
