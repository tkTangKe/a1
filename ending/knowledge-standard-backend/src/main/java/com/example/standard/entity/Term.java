package com.example.standard.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 术语实体
 * 对应数据库表：term
 */
@Data
@TableName("term")
public class Term {

    @TableId(type = IdType.AUTO)
    private Integer id;

    private String name;            // 术语名称
    private String definition;      // 定义内容
    private String category;        // 分类：基础设施/数据资源/应用系统/支撑体系
    private String sourceStandard;  // 来源标准编号，如 T/CIATCM 001—2019
    private Integer standardDocId;  // 关联的标准文档ID
}