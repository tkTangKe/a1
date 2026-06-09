package com.example.standard.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 代码表实体（如“设置/主办单位代码表”）
 * 对应数据库表：code_table
 */
@Data
@TableName("code_table")
public class CodeTable {

    @TableId(type = IdType.AUTO)
    private Integer id;

    private String name;          // 表名称，如“设置/主办单位代码表”
    private String code;          // 表标识，如 CV08.10.A09
    private Integer standardDocId; // 所属标准文档ID（外键）
}