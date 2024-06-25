package com.anihub.user.service;

import com.anihub.model.common.dtos.Result;
import com.anihub.model.user.dtos.LoginDto;
import com.anihub.model.user.pojos.User;
import com.anihub.model.user.vo.LoginVo;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;


public interface IUserService extends IService<User> {
    /**
     * 注册
     * @param loginDto
     */
    void register(LoginDto loginDto);

    /**
     * 登录
     * @param loginDto
     * @return
     */
    LoginVo login(LoginDto loginDto);

    List<User> selectBatchIds(List<Long> userIds);
}
