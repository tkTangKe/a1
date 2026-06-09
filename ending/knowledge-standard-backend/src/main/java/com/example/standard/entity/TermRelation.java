package com.example.standard.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 术语之间的关系实体
 * 对应数据库表：term_relation
 */
@Data
@TableName("term_relation")
public class TermRelation {

    @TableId(type = IdType.AUTO)
    private Integer id;

    private Integer sourceTermId;   // 源术语ID
    private Integer targetTermId;   // 目标术语ID
    private String relationType;    // 关系类型：包含、相关、参见等
}