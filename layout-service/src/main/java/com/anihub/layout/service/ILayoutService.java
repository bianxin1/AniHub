package com.anihub.layout.service;

import com.anihub.model.layout.dtos.LayoutDto;
import com.anihub.model.layout.pojos.Layout;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface ILayoutService extends IService<Layout> {
    /**
     * 添加版面
     *
     * @param layoutDto
     */
    void add(LayoutDto layoutDto);

    /**
     * 获取所有父版面id
     *
     * @return
     */
    List<Long> getAllParentId();

    /**
     * 根据父版面id获取子版面id
     * @param parentId
     * @return
     */
    List<Long> getLayoutIdByParentId(Long parentId);
}