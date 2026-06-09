package com.example.standard.service;

import java.util.List;
import java.util.Map;

public interface StatisticsService {
    Map<String, Long> getDashboardStats();
    Map<String, Long> getTermCategoryDistribution();
    Map<String, Long> getCodeTableStats();
    Map<String, Long> getYearlyTrend();
    Map<String, Integer> getWordCloudData();

    // ========== 钻取接口：获取某个分类下各项目的数量 ==========
    List<Map<String, Object>> getCategoryDetail(String category);
}