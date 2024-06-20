package com.anihub.model.post.dtos;

import lombok.Data;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
public class PostRedisDto {
    private Long id;
    private Long userId;
    private String username;
    private String lastCommentUserId;
    private String lastCommentUsername;
    private String title;
    private Long commentCount;
    private Date createdAt;
    private Date commentAt;
    private List<Long> tags = new ArrayList<>();
    private List<String> tagName = new ArrayList<>();
}
