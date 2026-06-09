package com.example.standard.service;

public interface LogService {
    void saveLog(String operation, String targetType, Integer targetId, String detail, String username, String ip);
}