package com.example.standard.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.standard.entity.Feedback;
import com.example.standard.mapper.FeedbackMapper;
import com.example.standard.service.FeedbackService;
import org.springframework.stereotype.Service;

@Service
public class FeedbackServiceImpl extends ServiceImpl<FeedbackMapper, Feedback> implements FeedbackService {
}