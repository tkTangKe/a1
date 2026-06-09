//package com.example.standard.controller;
//
//import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
//import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
//import com.example.standard.entity.OperationLog;
//import com.example.standard.mapper.OperationLogMapper;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.util.StringUtils;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.HashMap;
//import java.util.Map;
//
//@RestController
//@RequestMapping("/api/logs")
//public class LogController {
//
//    @Autowired
//    private OperationLogMapper operationLogMapper;
//
//    @GetMapping
//    public Map<String, Object> listLogs(
//            @RequestParam(required = false) String username,
//            @RequestParam(required = false) String operation,
//            @RequestParam(required = false) String startDate,
//            @RequestParam(required = false) String endDate,
//            @RequestParam(defaultValue = "1") int page,
//            @RequestParam(defaultValue = "20") int size) {
//
//        Page<OperationLog> pageObj = new Page<>(page, size);
//        LambdaQueryWrapper<OperationLog> wrapper = new LambdaQueryWrapper<>();
//        if (StringUtils.hasText(username)) wrapper.like(OperationLog::getUsername, username);
//        if (StringUtils.hasText(operation)) wrapper.eq(OperationLog::getOperation, operation);
//        if (StringUtils.hasText(startDate)) wrapper.ge(OperationLog::getCreateTime, startDate + " 00:00:00");
//        if (StringUtils.hasText(endDate)) wrapper.le(OperationLog::getCreateTime, endDate + " 23:59:59");
//        wrapper.orderByDesc(OperationLog::getCreateTime);
//        Page<OperationLog> result = operationLogMapper.selectPage(pageObj, wrapper);
//
//        Map<String, Object> response = new HashMap<>();
//        response.put("code", 200);
//        response.put("data", result);
//        return response;
//    }
//}
package com.example.standard.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.standard.entity.OperationLog;
import com.example.standard.mapper.OperationLogMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/logs")
public class LogController {

    private static final Logger log = LoggerFactory.getLogger(LogController.class);

    @Autowired
    private OperationLogMapper operationLogMapper;

    @GetMapping
    public Map<String, Object> listLogs(
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String operation,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size) {

        Page<OperationLog> pageObj = new Page<>(page, size);
        LambdaQueryWrapper<OperationLog> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(username)) wrapper.like(OperationLog::getUsername, username);
        if (StringUtils.hasText(operation)) wrapper.eq(OperationLog::getOperation, operation);
        if (StringUtils.hasText(startDate)) wrapper.ge(OperationLog::getCreateTime, startDate + " 00:00:00");
        if (StringUtils.hasText(endDate)) wrapper.le(OperationLog::getCreateTime, endDate + " 23:59:59");
        wrapper.orderByDesc(OperationLog::getCreateTime);
        Page<OperationLog> result = operationLogMapper.selectPage(pageObj, wrapper);

        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("data", result);
        return response;
    }

    @PostMapping
    public Map<String, Object> addLog(@RequestBody OperationLog logEntity, HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();
        try {
            // 打印接收到的数据
            log.info("接收到日志: username={}, operation={}, targetType={}, targetId={}, detail={}",
                    logEntity.getUsername(), logEntity.getOperation(), logEntity.getTargetType(), logEntity.getTargetId(), logEntity.getDetail());

            // 获取IP
            String ip = request.getRemoteAddr();
            String forwarded = request.getHeader("X-Forwarded-For");
            if (forwarded != null && !forwarded.isEmpty()) {
                ip = forwarded.split(",")[0].trim();
            }
            logEntity.setIp(ip);
            logEntity.setCreateTime(LocalDateTime.now());

            // 插入数据库
            int rows = operationLogMapper.insert(logEntity);
            log.info("日志插入结果: {}", rows);

            response.put("code", 200);
            response.put("message", "日志记录成功");
        } catch (Exception e) {
            log.error("日志记录失败", e);
            response.put("code", 500);
            response.put("message", "日志记录失败: " + e.getMessage());
        }
        return response;
    }
}