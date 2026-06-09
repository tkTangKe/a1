package com.example.standard.controller;

import com.example.standard.dto.GraphDataDto;
import com.example.standard.service.TermRelationService;
import com.example.standard.service.TermService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * 知识图谱数据接口
 * 返回前端 G6 所需的 nodes 和 edges 格式
 */
@RestController
@RequestMapping("/api/knowledge-graph")
public class KnowledgeGraphController {

    @Autowired
    private TermService termService;

    @Autowired
    private TermRelationService relationService;

    /**
     * 获取完整知识图谱数据（所有节点和边）
     */
    @GetMapping("/data")
    public GraphDataDto getGraphData() {
        // 查询所有术语作为节点
        List<Map<String, Object>> nodes = termService.getGraphNodes(); // 自定义方法，返回 {id, label, category, definition, source}
        // 查询所有关系作为边
        List<Map<String, Object>> edges = relationService.getGraphEdges(); // 返回 {source, target, label}
        GraphDataDto dto = new GraphDataDto();
        dto.setNodes(nodes);
        dto.setEdges(edges);
        return dto;
    }

    /**
     * 按标准来源筛选图谱数据
     */
    @GetMapping("/filterBySource")
    public GraphDataDto getGraphDataBySource(@RequestParam("source") String sourceStandard) {
        // 查询特定来源标准的术语及其关系
        List<Map<String, Object>> nodes = termService.getGraphNodesBySource(sourceStandard);
        List<Map<String, Object>> edges = relationService.getGraphEdgesBySource(sourceStandard);
        GraphDataDto dto = new GraphDataDto();
        dto.setNodes(nodes);
        dto.setEdges(edges);
        return dto;
    }

    /**
     * 按术语分类筛选图谱数据
     */
    @GetMapping("/filterByCategory")
    public GraphDataDto getGraphDataByCategory(@RequestParam("category") String category) {
        List<Map<String, Object>> nodes = termService.getGraphNodesByCategory(category);
        List<Map<String, Object>> edges = relationService.getGraphEdgesByCategory(category);
        GraphDataDto dto = new GraphDataDto();
        dto.setNodes(nodes);
        dto.setEdges(edges);
        return dto;
    }
}