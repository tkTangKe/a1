package com.example.standard.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.standard.entity.Favorite;
import java.util.List;
import java.util.Map;

public interface FavoriteService extends IService<Favorite> {
    boolean addFavorite(Long userId, String targetType, Long targetId);
    boolean removeFavorite(Long userId, String targetType, Long targetId);
    List<Map<String, Object>> listFavoritesByUser(Long userId, String targetType);
    boolean isFavorited(Long userId, String targetType, Long targetId);
}