package com.anihub.model.post.vo;

import lombok.Data;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
public class PostVo {
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
    //private List<String> tags = new ArrayList<>();
}
