package com.example.standard.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.standard.entity.Term;
import com.example.standard.mapper.TermMapper;
import com.example.standard.service.TermService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class TermServiceImpl extends ServiceImpl<TermMapper, Term> implements TermService {

    @Override
    public List<Map<String, Object>> getGraphNodes() {
        List<Term> terms = this.list();
        List<Map<String, Object>> nodes = new ArrayList<>();
        for (Term term : terms) {
            Map<String, Object> node = new HashMap<>();
            node.put("id", term.getName());  // G6 使用 name 作为 id
            node.put("label", term.getName());
            node.put("category", term.getCategory());
            node.put("definition", term.getDefinition());
            node.put("source", term.getSourceStandard());
            nodes.add(node);
        }
        return nodes;
    }

    @Override
    public List<Map<String, Object>> getGraphNodesBySource(String sourceStandard) {
        LambdaQueryWrapper<Term> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Term::getSourceStandard, sourceStandard);
        List<Term> terms = this.list(wrapper);
        List<Map<String, Object>> nodes = new ArrayList<>();
        for (Term term : terms) {
            Map<String, Object> node = new HashMap<>();
            node.put("id", term.getName());
            node.put("label", term.getName());
            node.put("category", term.getCategory());
            node.put("definition", term.getDefinition());
            node.put("source", term.getSourceStandard());
            nodes.add(node);
        }
        return nodes;
    }

    @Override
    public List<Map<String, Object>> getGraphNodesByCategory(String category) {
        LambdaQueryWrapper<Term> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Term::getCategory, category);
        List<Term> terms = this.list(wrapper);
        List<Map<String, Object>> nodes = new ArrayList<>();
        for (Term term : terms) {
            Map<String, Object> node = new HashMap<>();
            node.put("id", term.getName());
            node.put("label", term.getName());
            node.put("category", term.getCategory());
            node.put("definition", term.getDefinition());
            node.put("source", term.getSourceStandard());
            nodes.add(node);
        }
        return nodes;
    }
}