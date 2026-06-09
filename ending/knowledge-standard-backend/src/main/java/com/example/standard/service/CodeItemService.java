package com.example.standard.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.standard.entity.CodeItem;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface CodeItemService extends IService<CodeItem> {
    List<CodeItem> getTreeByTableId(Integer tableId);
    void importExcel(Integer tableId, MultipartFile file);
}