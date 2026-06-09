package com.example.standard.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.util.Date;

/**
 * 标准文档实体
 * 对应数据库表：standard_document
 */
@Data
@TableName("standard_document")
public class StandardDocument {

	@TableId(type = IdType.AUTO)
	private Integer id;

	private String code;          // 标准编号，如 T/CIATCM 005—2025
	private String name;          // 标准名称
	private Date publishDate;     // 发布日期
	private String version;       // 版本
	private String filePath;      // PDF存储路径
	private String contentText;   // 提取的全文内容（长文本）

	@TableField(fill = FieldFill.INSERT)
	private Date createTime;      // 创建时间，自动填充
}