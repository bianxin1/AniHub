package com.anihub.user.service.Impl;

import cn.hutool.core.bean.BeanUtil;
import com.anihub.model.user.dtos.LoginDto;
import com.anihub.model.user.pojos.User;
import com.anihub.model.user.vo.LoginVo;
import com.anihub.user.mapper.UserMapper;
import com.anihub.user.service.IUserService;
import com.anihub.utils.common.AppJwtUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {
    final private UserMapper userMapper;

    /**
     * 注册
     *
     * @param loginDto
     */
    @Override
    public void register(LoginDto loginDto) {
        // 1.数据校验查询数据库中是否有相同用户名
        User user = userMapper.selectById(loginDto.getUsername());
        if (user != null) {
            log.error("用户名已存在");
            throw new RuntimeException("用户名已存在");
        }
        // 2.数据封装
        user = new User();
        BeanUtil.copyProperties(loginDto, user);
        //  3.密码加密
        // 3.1生成随机盐
        String salt = AppJwtUtil.getRandomSalt();
        user.setSalt(salt);
        // 3.2MD5密码加密
        String pasw = DigestUtils.md5DigestAsHex((loginDto.getPassword() + salt).getBytes());
        user.setPassword(pasw);
        // 4.数据插入
        userMapper.insert(user);
    }

    /**
     * 登录
     *
     * @param loginDto
     * @return
     */
    @Override
    public LoginVo login(LoginDto loginDto) {
        // 1查询数据库中是否有相同用户名,查询用户名username而不是id
        User user = getOne(Wrappers.<User>lambdaQuery().eq(User::getUsername, loginDto.getUsername()));
        if (user == null) {
            log.error("用户名不存在");
            throw new RuntimeException("用户名不存在");
        }
        // 2.md5+salt密码校验
        String pasw = DigestUtils.md5DigestAsHex((loginDto.getPassword() + user.getSalt()).getBytes());
        if (!pasw.equals(user.getPassword())) {
            log.error("密码错误");
            throw new RuntimeException("密码错误");
        }
        // 3.生成jwt令牌
        String token = AppJwtUtil.getToken(user.getId().longValue());
        // 4.返回结果
        LoginVo loginVo = new LoginVo();
        loginVo.setToken(token);
        BeanUtil.copyProperties(user, loginVo);
        return loginVo;

    }
}
