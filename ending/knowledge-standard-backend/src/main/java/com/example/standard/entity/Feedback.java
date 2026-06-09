package com.example.standard.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("feedback")
public class Feedback {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private String username;
    private String title;
    private String content;
    private LocalDateTime createTime;
    private Integer status;   // 0:未读, 1:已读

    // ========== 新增字段 ==========
    private String reply;          // 管理员回复内容
    private LocalDateTime replyTime; // 回复时间
}