package com.example.standard.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.standard.entity.Term;

import java.util.List;
import java.util.Map;

public interface TermService extends IService<Term> {
    List<Map<String, Object>> getGraphNodes();
    List<Map<String, Object>> getGraphNodesBySource(String sourceStandard);
    List<Map<String, Object>> getGraphNodesByCategory(String category);
}