package com.anihub.layout.service;

import com.anihub.model.layout.dtos.LayoutDto;
import com.anihub.model.layout.pojos.Layout;
import com.baomidou.mybatisplus.extension.service.IService;

public interface ILayoutService extends IService<Layout> {
    /**
     * 添加版面
     * @param layoutDto
     */
    void add(LayoutDto layoutDto);

}
