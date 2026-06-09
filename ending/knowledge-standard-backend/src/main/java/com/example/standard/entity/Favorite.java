package com.example.standard.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.util.Date;

@Data
@TableName("favorite")
public class Favorite {
    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;

    private String targetType;   // 'document', 'content', 'term'

    private Long targetId;

    @TableField(fill = FieldFill.INSERT)
    private Date createdAt;
}