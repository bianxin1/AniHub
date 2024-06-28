package com.anihub.layout.service.Impl;

import cn.hutool.core.bean.BeanUtil;
import com.anihub.layout.mapper.LayoutMapper;
import com.anihub.layout.service.ILayoutService;
import com.anihub.model.layout.dtos.LayoutDto;
import com.anihub.model.layout.pojos.Layout;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class LayoutServiceImpl extends ServiceImpl<LayoutMapper, Layout> implements ILayoutService{
    private final LayoutMapper layoutMapper;
    /**
     * 添加版面
     * @param layoutDto
     */
    @Override
    public void add(LayoutDto layoutDto) {
        Layout layout =new Layout();
        BeanUtil.copyProperties(layoutDto, layout);
        baseMapper.insert(layout);
    }

    /**
     * 获取所有父版面id
     * @return
     */
    @Override
    public List<Long> getAllParentId() {
        return layoutMapper.getAllParentId();
    }

    /**
     * 根据父版面id获取子版面id
     * @param parentId
     * @return
     */
    @Override
    public List<Long> getLayoutIdByParentId(Long parentId) {
        return layoutMapper.getLayoutIdByParentId(parentId);
    }
}
