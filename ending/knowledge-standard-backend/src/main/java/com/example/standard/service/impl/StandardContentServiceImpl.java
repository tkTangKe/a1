package com.example.standard.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.standard.entity.StandardContent;
import com.example.standard.mapper.StandardContentMapper;
import com.example.standard.service.StandardContentService;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class StandardContentServiceImpl extends ServiceImpl<StandardContentMapper, StandardContent> implements StandardContentService {
    @Override
    public List<StandardContent> listByDocId(Integer docId) {
        LambdaQueryWrapper<StandardContent> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(StandardContent::getStandardDocId, docId)
                .orderByAsc(StandardContent::getSortOrder);
        return this.list(wrapper);
    }
}