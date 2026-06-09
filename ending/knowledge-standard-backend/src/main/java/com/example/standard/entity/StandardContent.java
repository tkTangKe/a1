package com.example.standard.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("standard_content")
public class StandardContent {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private Integer standardDocId;   // 关联标准文档ID
    private String category;         // 一级分类
    private String subCategory;      // 二级分类（可选）
    private String itemName;         // 具体项目名称
    private String content;          // 详细内容
    private Integer sortOrder;       // 排序
}