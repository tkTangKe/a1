package com.example.standard.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.standard.entity.StandardContent;
import java.util.List;

public interface StandardContentService extends IService<StandardContent> {
    List<StandardContent> listByDocId(Integer docId);
}