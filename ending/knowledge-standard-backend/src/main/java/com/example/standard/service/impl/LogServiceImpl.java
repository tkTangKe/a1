package com.example.standard.service.impl;

import com.example.standard.entity.OperationLog;
import com.example.standard.mapper.OperationLogMapper;
import com.example.standard.service.LogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class LogServiceImpl implements LogService {

    @Autowired
    private OperationLogMapper operationLogMapper;

    @Override
    public void saveLog(String operation, String targetType, Integer targetId, String detail, String username, String ip) {
        try {
            OperationLog log = new OperationLog();
            log.setUsername(username);
            log.setOperation(operation);
            log.setTargetType(targetType);
            log.setTargetId(String.valueOf(targetId));
            log.setDetail(detail);
            log.setIp(ip);
            log.setCreateTime(LocalDateTime.now());
            operationLogMapper.insert(log);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}