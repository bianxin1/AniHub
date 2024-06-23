package com.anihub.user.service.Impl;

import com.anihub.model.user.pojos.Folder;
import com.anihub.user.mapper.FolderMapper;
import com.anihub.user.service.ICollectService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class CollectServiceImpl extends ServiceImpl<FolderMapper, Folder> implements ICollectService {
}
