package com.example.standard.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.standard.entity.CodeTable;
import com.example.standard.mapper.CodeTableMapper;
import com.example.standard.service.CodeTableService;
import org.springframework.stereotype.Service;

@Service
public class CodeTableServiceImpl extends ServiceImpl<CodeTableMapper, CodeTable> implements CodeTableService {
}