package com.anihub.model.post.vo;

import lombok.Data;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
public class PostVo {
    private Long userId;
    private String username;
    private String avatar;
    private Integer grade;
    private Integer likeCount;
    private String title;
    private String content;
    private Date createdAt;
    private List<Long> tags = new ArrayList<>();
    private List<String> tagNames = new ArrayList<>();
}
