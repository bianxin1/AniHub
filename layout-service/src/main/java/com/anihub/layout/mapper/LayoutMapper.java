package com.anihub.layout.mapper;

import com.anihub.model.layout.pojos.Layout;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface LayoutMapper extends BaseMapper<Layout> {
    @Select("select id from layout where parent_id is null")
    List<Long> getAllParentId();
    @Select("select id from layout where parent_id = #{parentId}")
    List<Long> getLayoutIdByParentId(Long parentId);
}
