package com.anihub.user.controller;

import cn.hutool.core.bean.BeanUtil;
import com.anihub.common.utils.UserContext;
import com.anihub.model.common.dtos.Result;
import com.anihub.model.post.dtos.PostRedisDto;
import com.anihub.model.user.dtos.FolderDto;
import com.anihub.model.user.pojos.Folder;
import com.anihub.model.user.vo.FolderVo;
import com.anihub.user.service.ICollectService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(tags = "收藏")
@RestController
@RequestMapping("/collect")
@RequiredArgsConstructor
@Slf4j
public class CollectController {
    private final ICollectService collectService;
    @ApiOperation("添加收藏夹")
    @PostMapping("/add")
    public Result add(@RequestBody FolderDto folderDto) {
        log.info("添加收藏夹");
        Folder folder =new Folder();
        folder.setUserId(UserContext.getUser());
        BeanUtil.copyProperties(folderDto,folder);
        collectService.save(folder);
        return Result.success();
    }
    @ApiOperation("删除收藏夹")
    @DeleteMapping("/delete")
    public Result delete(@RequestParam("id") Long id) {
        log.info("删除收藏夹");
        UpdateWrapper<Folder> updateWrapper = new UpdateWrapper<>();
        updateWrapper.set("status", 2).eq("id", id);
        collectService.update(updateWrapper);
        return Result.success();
    }
    @ApiOperation("修改收藏夹")
    @PutMapping("/update")
    public Result update(@RequestBody FolderDto folderDto) {
        log.info("修改收藏夹");
        Folder folder =new Folder();
        BeanUtil.copyProperties(folderDto,folder);
        folder.setId(folderDto.getFolderId());
        collectService.updateById(folder);
        return Result.success();
    }
    @ApiOperation("查询收藏夹")
    @GetMapping("/select")
    public Result<List<FolderVo>> select() {
        log.info("查询收藏夹");
        // 查询当前用户收藏夹
        Long user = UserContext.getUser();
        QueryWrapper<Folder> queryWrapper = new QueryWrapper<>();
        // 查询条件userid = user ,status <2
        queryWrapper.eq("user_id",user).lt("status",2);
        List<Folder> folder = collectService.list(queryWrapper);
        List<FolderVo> folderVo = BeanUtil.copyToList(folder, FolderVo.class);
        return Result.success(folderVo);
    }
}
