package com.example.standard.controller;

import com.example.standard.entity.Feedback;
import com.example.standard.service.FeedbackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/feedback")
public class FeedbackController {

    @Autowired
    private FeedbackService feedbackService;

    @GetMapping
    public List<Feedback> getAll() {
        return feedbackService.list();
    }

    @PostMapping
    public ResponseEntity<?> add(@RequestBody Feedback feedback) {
        if (feedback.getUsername() == null || feedback.getTitle() == null || feedback.getContent() == null) {
            return ResponseEntity.badRequest().body("参数不全");
        }
        feedback.setStatus(0);
        feedback.setCreateTime(LocalDateTime.now());
        feedbackService.save(feedback);
        return ResponseEntity.ok("success");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Integer id) {
        feedbackService.removeById(id);
        return ResponseEntity.ok("success");
    }

    // 回复接口（关键）
    @PutMapping("/{id}/reply")
    public ResponseEntity<?> replyFeedback(@PathVariable Integer id, @RequestBody Map<String, String> payload) {
        String reply = payload.get("reply");
        if (reply == null || reply.trim().isEmpty()) {
            return ResponseEntity.badRequest().body("回复内容不能为空");
        }
        Feedback feedback = feedbackService.getById(id);
        if (feedback == null) {
            return ResponseEntity.notFound().build();
        }
        feedback.setReply(reply);
        feedback.setReplyTime(LocalDateTime.now());
        feedbackService.updateById(feedback);
        return ResponseEntity.ok("回复成功");
    }
}