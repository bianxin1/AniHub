package com.anihub.model.user.vo;

import lombok.Data;

@Data
public class LoginVo {
    //用户名
    private String username;
    //token
    private String token;
    //用户id
    private Integer id;
}
