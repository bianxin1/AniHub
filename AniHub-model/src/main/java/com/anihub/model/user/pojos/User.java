package com.anihub.model.user.pojos;

import lombok.Data;

import java.util.Date;

//用户实体类

@Data
public class User {
    //主键，自动递增
    private Long id;
    //用户名
    private String username;
    //邮箱
    private String email;
    //手机号
    private String phone;
    //密码
    private String password;
    //密码盐值
    private String salt;
    //经验值
    private Integer exp;
    //等级
    private Integer grade;
    //头像URL
    private String avatar;
    //签名
    private String sign;
    //粉丝数
    private Integer fanCount;
    //关注数
    private Integer followCount;
    //点赞数
    private Integer likeCount;
    //创建时间
    private Date createdTime;
    //更新时间
    private Date updatedTime;
    //用户状态
    private Integer status;

}
