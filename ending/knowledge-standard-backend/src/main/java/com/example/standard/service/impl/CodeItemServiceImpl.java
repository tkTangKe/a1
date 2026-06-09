package com.example.standard.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.standard.entity.CodeItem;
import com.example.standard.mapper.CodeItemMapper;
import com.example.standard.service.CodeItemService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Service
public class CodeItemServiceImpl extends ServiceImpl<CodeItemMapper, CodeItem> implements CodeItemService {

    @Override
    public List<CodeItem> getTreeByTableId(Integer tableId) {
        // 查询该代码表下所有代码项
        LambdaQueryWrapper<CodeItem> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CodeItem::getCodeTableId, tableId);
        List<CodeItem> allItems = this.list(wrapper);
        // 构建树形结构
        return buildTree(allItems, null);
    }

    private List<CodeItem> buildTree(List<CodeItem> allItems, Integer parentId) {
        List<CodeItem> tree = new ArrayList<>();
        for (CodeItem item : allItems) {
            if (parentId == null && item.getParentId() == null) {
                // 顶级节点
                item.setChildren(buildTree(allItems, item.getId()));
                tree.add(item);
            } else if (parentId != null && parentId.equals(item.getParentId())) {
                item.setChildren(buildTree(allItems, item.getId()));
                tree.add(item);
            }
        }
        return tree;
    }

    @Override
    public void importExcel(Integer tableId, MultipartFile file) {
        // 实际上使用 EasyExcel 读取并批量插入，这里简化，避免代码过多
        // 你可以在后期实现。暂时抛出未实现异常或留空。
        throw new UnsupportedOperationException("Excel导入暂未实现，请通过前端逐个添加");
    }
}