package com.example.standard.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.standard.entity.*;
import com.example.standard.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class StatisticsServiceImpl implements StatisticsService {

    @Autowired
    private StandardDocumentService documentService;
    @Autowired
    private CodeTableService codeTableService;
    @Autowired
    private CodeItemService codeItemService;
    @Autowired
    private TermService termService;
    @Autowired
    private StandardContentService contentService;  // 新增，用于钻取和词云

    @Override
    public Map<String, Long> getDashboardStats() {
        Map<String, Long> stats = new HashMap<>();
        stats.put("docCount", (long) documentService.count());
        stats.put("codeItemCount", (long) codeItemService.count());
        stats.put("termCount", (long) termService.count());
        return stats;
    }

    @Override
    public Map<String, Long> getTermCategoryDistribution() {
        List<Term> terms = termService.list();
        return terms.stream()
                .filter(t -> t.getCategory() != null && !t.getCategory().trim().isEmpty())
                .collect(Collectors.groupingBy(Term::getCategory, Collectors.counting()));
    }

    @Override
    public Map<String, Long> getCodeTableStats() {
        List<CodeTable> tables = codeTableService.list();
        Map<String, Long> result = new LinkedHashMap<>();
        for (CodeTable table : tables) {
            LambdaQueryWrapper<CodeItem> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(CodeItem::getCodeTableId, table.getId());
            long count = codeItemService.count(wrapper);
            result.put(table.getName(), count);
        }
        return result;
    }

    @Override
    public Map<String, Long> getYearlyTrend() {
        List<StandardDocument> docs = documentService.list();
        Map<String, Long> trend = new TreeMap<>();
        for (StandardDocument doc : docs) {
            if (doc.getPublishDate() != null) {
                Calendar cal = Calendar.getInstance();
                cal.setTime(doc.getPublishDate());
                String year = String.valueOf(cal.get(Calendar.YEAR));
                trend.put(year, trend.getOrDefault(year, 0L) + 1);
            }
        }
        return trend;
    }

    @Override
    public Map<String, Integer> getWordCloudData() {
        // 从 standard_content 表中统计 itemName 和 category 的原始出现次数（整数）
        List<StandardContent> contents = contentService.list();
        Map<String, Integer> freq = new HashMap<>();

        for (StandardContent sc : contents) {
            // 统计项目名称（作为一个完整关键词）
            String item = sc.getItemName();
            if (item != null && !item.trim().isEmpty()) {
                freq.put(item, freq.getOrDefault(item, 0) + 1);
            }
            // 统计分类（也作为关键词，使词云更丰富）
            String category = sc.getCategory();
            if (category != null && !category.trim().isEmpty()) {
                freq.put(category, freq.getOrDefault(category, 0) + 1);
            }
        }

        // 按频次降序排序，取前30个高频词返回
        return freq.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .limit(30)
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e1,
                        LinkedHashMap::new
                ));
    }

    // ========== 钻取接口：获取某个分类下各项目的数量 ==========
    @Override
    public List<Map<String, Object>> getCategoryDetail(String category) {
        List<StandardContent> contents = contentService.lambdaQuery()
                .eq(StandardContent::getCategory, category)
                .list();
        Map<String, Long> countMap = contents.stream()
                .filter(c -> c.getItemName() != null && !c.getItemName().isEmpty())
                .collect(Collectors.groupingBy(StandardContent::getItemName, Collectors.counting()));
        List<Map<String, Object>> result = new ArrayList<>();
        for (Map.Entry<String, Long> entry : countMap.entrySet()) {
            Map<String, Object> item = new HashMap<>();
            item.put("name", entry.getKey());
            item.put("value", entry.getValue());
            result.add(item);
        }
        result.sort((a, b) -> ((Long) b.get("value")).compareTo((Long) a.get("value")));
        return result;
    }
}