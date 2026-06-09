package com.example.standard.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.standard.entity.TermRelation;

import java.util.List;
import java.util.Map;

public interface TermRelationService extends IService<TermRelation> {
    List<Map<String, Object>> getGraphEdges();
    List<Map<String, Object>> getGraphEdgesBySource(String sourceStandard);
    List<Map<String, Object>> getGraphEdgesByCategory(String category);
}