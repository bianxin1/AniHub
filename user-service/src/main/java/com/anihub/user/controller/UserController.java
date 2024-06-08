package com.anihub.user.controller;

import com.anihub.model.common.dtos.Result;
import com.anihub.model.user.dtos.LoginDto;
import com.anihub.model.user.vo.LoginVo;
import com.anihub.user.service.IUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Api(tags = "用户管理")
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final IUserService userService;
    @ApiOperation("注册")
    @PostMapping("/register")
    public Result register(@RequestBody LoginDto loginDto) {
        // 注册
        userService.register(loginDto);
        return Result.success();
    }
    @ApiOperation("登录")
    @PostMapping("/login")
    public Result login(@RequestBody LoginDto loginDto) {
        LoginVo LoginVo = userService.login(loginDto);

        // 登录
        return Result.success(LoginVo);
    }
    @GetMapping("/test")
    public String test() {
        return "test";
    }
}
