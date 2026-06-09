package com.example.standard.controller;

import com.example.standard.entity.User;
import com.example.standard.mapper.UserMapper;
import com.example.standard.mapper.UserRoleMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private UserRoleMapper userRoleMapper;

    @PostMapping("/login")
    public Map<String, Object> login(@RequestBody Map<String, String> loginInfo) {
        String username = loginInfo.get("username");
        String password = loginInfo.get("password");
        Map<String, Object> result = new HashMap<>();

        // 根据用户名查用户
        User user = userMapper.selectOne(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<User>()
                        .eq(User::getUsername, username)
        );

        if (user == null || !password.equals(user.getPassword())) {
            result.put("code", 401);
            result.put("message", "用户名或密码错误");
            return result;
        }

        // 获取用户角色（ADMIN 或 VIEWER）
        String roleName = userRoleMapper.getRoleNameByUserId(user.getId());
        if (roleName == null) {
            roleName = "VIEWER";   // 默认角色
        }

        // 生成简单 token
        String token = Base64.getEncoder().encodeToString((username + ":" + roleName).getBytes());
        result.put("code", 200);
        Map<String, String> data = new HashMap<>();
        data.put("token", token);
        data.put("role", roleName);
        result.put("data", data);
        return result;
    }
}