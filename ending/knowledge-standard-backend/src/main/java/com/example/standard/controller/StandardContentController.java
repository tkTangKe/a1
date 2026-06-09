package com.example.standard.controller;

import com.example.standard.entity.StandardContent;
import com.example.standard.service.StandardContentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/standard-contents")
public class StandardContentController {

    @Autowired
    private StandardContentService contentService;

    // 获取某个标准文档下的所有内容条目
    @GetMapping("/by-doc/{docId}")
    public List<StandardContent> listByDoc(@PathVariable Integer docId) {
        return contentService.listByDocId(docId);
    }

    // 新增一条内容
    @PostMapping
    public boolean add(@RequestBody StandardContent content) {
        return contentService.save(content);
    }

    // 更新内容
    @PutMapping
    public boolean update(@RequestBody StandardContent content) {
        return contentService.updateById(content);
    }

    // 删除内容
    @DeleteMapping("/{id}")
    public boolean delete(@PathVariable Integer id) {
        return contentService.removeById(id);
    }
}