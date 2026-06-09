package com.example.standard.controller;

import com.example.standard.service.FavoriteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/favorites")
public class FavoriteController {

    @Autowired
    private FavoriteService favoriteService;

    // 临时写死用户 ID，确保数据库中存在 id=1 的用户
    private Long getCurrentUserId(HttpServletRequest request) {
        // 正式环境请替换为真正的 JWT 解析
        return 1L;
    }

    @PostMapping
    public Map<String, Object> addFavorite(@RequestBody Map<String, Object> params, HttpServletRequest request) {
        Map<String, Object> result = new HashMap<>();
        try {
            Long userId = getCurrentUserId(request);
            Object typeObj = params.get("targetType");
            Object idObj = params.get("targetId");
            if (typeObj == null || idObj == null) {
                result.put("success", false);
                result.put("message", "参数不完整");
                return result;
            }
            String targetType = typeObj.toString();
            Long targetId;
            try {
                targetId = Long.valueOf(idObj.toString());
            } catch (NumberFormatException e) {
                result.put("success", false);
                result.put("message", "targetId 必须是数字");
                return result;
            }
            boolean success = favoriteService.addFavorite(userId, targetType, targetId);
            result.put("success", success);
        } catch (Exception e) {
            e.printStackTrace();
            result.put("success", false);
        }
        return result;
    }

    @DeleteMapping
    public Map<String, Object> removeFavorite(@RequestParam String targetType, @RequestParam Long targetId, HttpServletRequest request) {
        Map<String, Object> result = new HashMap<>();
        try {
            Long userId = getCurrentUserId(request);
            boolean success = favoriteService.removeFavorite(userId, targetType, targetId);
            result.put("success", success);
        } catch (Exception e) {
            result.put("success", false);
        }
        return result;
    }

    @GetMapping
    public Map<String, Object> listFavorites(@RequestParam(required = false) String type, HttpServletRequest request) {
        Map<String, Object> result = new HashMap<>();
        try {
            Long userId = getCurrentUserId(request);
            List<Map<String, Object>> data = favoriteService.listFavoritesByUser(userId, type);
            result.put("data", data);
        } catch (Exception e) {
            result.put("data", List.of());
        }
        return result;
    }

    @GetMapping("/check")
    public Map<String, Boolean> checkFavorited(@RequestParam String targetType, @RequestParam Long targetId, HttpServletRequest request) {
        Map<String, Boolean> result = new HashMap<>();
        try {
            Long userId = getCurrentUserId(request);
            boolean favorited = favoriteService.isFavorited(userId, targetType, targetId);
            result.put("favorited", favorited);
        } catch (Exception e) {
            result.put("favorited", false);
        }
        return result;
    }
}