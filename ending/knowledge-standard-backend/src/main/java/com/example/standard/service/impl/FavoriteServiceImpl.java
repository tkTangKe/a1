package com.example.standard.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.standard.entity.*;
import com.example.standard.mapper.FavoriteMapper;
import com.example.standard.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class FavoriteServiceImpl extends ServiceImpl<FavoriteMapper, Favorite> implements FavoriteService {

    @Autowired
    private StandardDocumentService documentService;
    @Autowired
    private StandardContentService contentService;
    @Autowired
    private TermService termService;

    @Override
    public boolean addFavorite(Long userId, String targetType, Long targetId) {
        System.out.println("addFavorite: userId=" + userId + ", type=" + targetType + ", targetId=" + targetId);
        if (!existsTarget(targetType, targetId)) {
            System.out.println("existsTarget 返回 false，目标不存在");
            return false;
        }
        Favorite favorite = new Favorite();
        favorite.setUserId(userId);
        favorite.setTargetType(targetType);
        favorite.setTargetId(targetId);
        favorite.setCreatedAt(new Date());
        boolean result = save(favorite);
        System.out.println("保存结果: " + result);
        return result;
    }

    @Override
    public boolean removeFavorite(Long userId, String targetType, Long targetId) {
        LambdaQueryWrapper<Favorite> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Favorite::getUserId, userId)
                .eq(Favorite::getTargetType, targetType)
                .eq(Favorite::getTargetId, targetId);
        return remove(wrapper);
    }

    @Override
    public List<Map<String, Object>> listFavoritesByUser(Long userId, String targetType) {
        System.out.println("listFavoritesByUser: userId=" + userId + ", type=" + targetType);
        LambdaQueryWrapper<Favorite> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Favorite::getUserId, userId)
                .eq(Favorite::getTargetType, targetType)
                .orderByDesc(Favorite::getCreatedAt);
        List<Favorite> favorites = list(wrapper);
        System.out.println("favorites 数量: " + favorites.size());
        if (favorites.isEmpty()) return Collections.emptyList();

        List<Map<String, Object>> result = new ArrayList<>();

        if ("document".equals(targetType)) {
            List<Integer> ids = favorites.stream()
                    .map(fav -> fav.getTargetId().intValue())
                    .collect(Collectors.toList());
            System.out.println("文档ID列表: " + ids);
            List<StandardDocument> docs = documentService.listByIds(ids);
            System.out.println("查到的文档数: " + docs.size());
            Map<Integer, StandardDocument> docMap = docs.stream()
                    .collect(Collectors.toMap(StandardDocument::getId, d -> d));
            for (Favorite fav : favorites) {
                StandardDocument doc = docMap.get(fav.getTargetId().intValue());
                if (doc != null) {
                    Map<String, Object> item = new HashMap<>();
                    item.put("id", doc.getId());
                    item.put("code", doc.getCode());
                    item.put("name", doc.getName());
                    item.put("publishDate", doc.getPublishDate());
                    item.put("favoriteId", fav.getId());
                    result.add(item);
                }
            }
        } else if ("content".equals(targetType)) {
            List<Integer> ids = favorites.stream()
                    .map(fav -> fav.getTargetId().intValue())
                    .collect(Collectors.toList());
            System.out.println("内容ID列表: " + ids);
            List<StandardContent> contents = contentService.listByIds(ids);
            System.out.println("查到的内容数: " + contents.size());
            Map<Integer, StandardContent> contentMap = contents.stream()
                    .collect(Collectors.toMap(StandardContent::getId, c -> c));
            for (Favorite fav : favorites) {
                StandardContent c = contentMap.get(fav.getTargetId().intValue());
                if (c != null) {
                    Map<String, Object> item = new HashMap<>();
                    item.put("id", c.getId());
                    item.put("category", c.getCategory());
                    item.put("itemName", c.getItemName());
                    item.put("content", c.getContent());
                    item.put("standardDocId", c.getStandardDocId());
                    item.put("favoriteId", fav.getId());
                    result.add(item);
                }
            }
        } else if ("term".equals(targetType)) {
            // 提取 targetId 列表（保持 Long 类型）
            List<Long> ids = favorites.stream()
                    .map(Favorite::getTargetId)
                    .collect(Collectors.toList());
            System.out.println("术语 targetId 列表(Long): " + ids);

            // 尝试按 Long 查询（如果 Term 主键是 Long）
            List<Term> terms = termService.lambdaQuery()
                    .in(Term::getId, ids)
                    .list();
            System.out.println("按 Long 查询到术语数: " + terms.size());

            // 如果按 Long 查询不到，尝试按 Integer 查询（兼容旧数据）
            if (terms.isEmpty() && !ids.isEmpty()) {
                List<Integer> intIds = ids.stream()
                        .map(Long::intValue)
                        .collect(Collectors.toList());
                System.out.println("尝试按 Integer 查询，ID列表: " + intIds);
                terms = termService.lambdaQuery()
                        .in(Term::getId, intIds)
                        .list();
                System.out.println("按 Integer 查询到术语数: " + terms.size());
            }

            // 构建映射：使用 Long 作为 key（统一转换）
            Map<Long, Term> termMap = new HashMap<>();
            for (Term term : terms) {
                // 安全转换：Number 类型都可以调用 longValue()
                Long termId = term.getId().longValue();
                termMap.put(termId, term);
            }

            for (Favorite fav : favorites) {
                Term term = termMap.get(fav.getTargetId());
                if (term != null) {
                    Map<String, Object> item = new HashMap<>();
                    item.put("id", term.getId());
                    item.put("name", term.getName());
                    item.put("definition", term.getDefinition());
                    item.put("category", term.getCategory());
                    item.put("favoriteId", fav.getId());
                    result.add(item);
                } else {
                    System.out.println("未找到术语，targetId=" + fav.getTargetId());
                }
            }
        }
        System.out.println("最终返回 result 数量: " + result.size());
        return result;
    }

    @Override
    public boolean isFavorited(Long userId, String targetType, Long targetId) {
        LambdaQueryWrapper<Favorite> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Favorite::getUserId, userId)
                .eq(Favorite::getTargetType, targetType)
                .eq(Favorite::getTargetId, targetId);
        return count(wrapper) > 0;
    }

    private boolean existsTarget(String targetType, Long targetId) {
        System.out.println("existsTarget: " + targetType + ", targetId=" + targetId);
        try {
            if ("document".equals(targetType)) {
                return documentService.getById(targetId.intValue()) != null;
            }
            if ("content".equals(targetType)) {
                return contentService.getById(targetId.intValue()) != null;
            }
            if ("term".equals(targetType)) {
                // 先尝试按 Long 查询，再按 Integer
                Term term = termService.getById(targetId);
                if (term == null) {
                    term = termService.getById(targetId.intValue());
                }
                return term != null;
            }
        } catch (Exception e) {
            System.err.println("existsTarget 异常: " + e.getMessage());
        }
        return false;
    }
}