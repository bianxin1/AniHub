package com.anihub.layout.service.Impl;

import com.anihub.layout.mapper.TagMapper;
import com.anihub.layout.service.ITagService;
import com.anihub.model.layout.pojos.Tag;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class TagServiceImpl extends ServiceImpl<TagMapper, Tag> implements ITagService {
}
