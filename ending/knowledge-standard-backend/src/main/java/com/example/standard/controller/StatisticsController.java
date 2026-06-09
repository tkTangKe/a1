package com.example.standard.controller;

import com.example.standard.service.StatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/statistics")
public class StatisticsController {

    @Autowired
    private StatisticsService statisticsService;

    @GetMapping("/dashboard")
    public Map<String, Long> getDashboardStats() {
        return statisticsService.getDashboardStats();
    }

    @GetMapping("/term-category-distribution")
    public Map<String, Long> getTermCategoryDistribution() {
        return statisticsService.getTermCategoryDistribution();
    }

    @GetMapping("/code-table-stats")
    public Map<String, Long> getCodeTableStats() {
        return statisticsService.getCodeTableStats();
    }

    @GetMapping("/trend")
    public Map<String, Long> getYearlyTrend() {
        return statisticsService.getYearlyTrend();
    }

    @GetMapping("/wordcloud")
    public Map<String, Integer> getWordCloudData() {
        return statisticsService.getWordCloudData();
    }

    /**
     * 分类钻取：根据分类名，返回该分类下各项目的计数（用于柱状图）
     * 调用 Service 层方法
     */
    @GetMapping("/term-category-detail")
    public List<Map<String, Object>> getCategoryDetail(@RequestParam String category) {
        return statisticsService.getCategoryDetail(category);
    }
}