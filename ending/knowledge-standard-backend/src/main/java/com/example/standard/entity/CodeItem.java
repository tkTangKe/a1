package com.example.standard.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.List;

/**
 * 代码项实体（支持父子层级，树形结构）
 * 对应数据库表：code_item
 */
@Data
@TableName("code_item")
public class CodeItem {

    @TableId(type = IdType.AUTO)
    private Integer id;

    private Integer codeTableId;  // 所属代码表ID
    private String code;          // 代码值，如 "1"
    private String value;         // 值含义，如 "卫生健康行政部门"
    private Integer parentId;     // 父级ID，根节点为0或null
    private Integer sortOrder;    // 排序序号

    // 非数据库字段，用于前端树形展示时临时存储子节点列表
    @TableField(exist = false)
    private List<CodeItem> children;
}