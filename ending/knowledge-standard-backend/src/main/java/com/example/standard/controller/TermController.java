package com.example.standard.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.standard.entity.Term;
import com.example.standard.service.TermService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 术语管理 Controller
 */
@RestController
@RequestMapping("/api/terms")
public class TermController {

    @Autowired
    private TermService termService;

    /**
     * 获取所有术语
     */
    @GetMapping
    public List<Term> listAll() {
        return termService.list();
    }

    /**
     * 根据ID获取术语详情
     */
    @GetMapping("/{id}")
    public Term getById(@PathVariable Integer id) {
        return termService.getById(id);
    }

    /**
     * 新增术语
     */
    @PostMapping
    public boolean add(@RequestBody Term term) {
        return termService.save(term);
    }

    /**
     * 更新术语
     */
    @PutMapping
    public boolean update(@RequestBody Term term) {
        return termService.updateById(term);
    }

    /**
     * 删除术语
     */
    @DeleteMapping("/{id}")
    public boolean delete(@PathVariable Integer id) {
        return termService.removeById(id);
    }

    /**
     * 按分类查询术语
     */
    @GetMapping("/category/{category}")
    public List<Term> listByCategory(@PathVariable String category) {
        LambdaQueryWrapper<Term> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Term::getCategory, category);
        return termService.list(wrapper);
    }

    /**
     * 按来源标准查询术语
     */
    @GetMapping("/source/{sourceStandard}")
    public List<Term> listBySource(@PathVariable String sourceStandard) {
        LambdaQueryWrapper<Term> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Term::getSourceStandard, sourceStandard);
        return termService.list(wrapper);
    }

    /**
     * 关键词搜索术语（名称或定义模糊匹配）
     */
    @GetMapping("/search")
    public List<Term> search(@RequestParam("keyword") String keyword) {
        LambdaQueryWrapper<Term> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(Term::getName, keyword)
                .or()
                .like(Term::getDefinition, keyword);
        return termService.list(wrapper);
    }
}