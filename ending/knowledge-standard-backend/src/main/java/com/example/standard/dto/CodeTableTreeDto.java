package com.example.standard.dto;

import lombok.Data;

import java.util.List;

    /**
     * 代码表树形结构 DTO
     * 用于返回某个代码表及其完整的树形代码项
     */
    @Data
    public class CodeTableTreeDto {

        private Integer id;                 // 代码表ID
        private String name;               // 代码表名称
        private String code;               // 代码表标识，如 CV08.10.A09
        private Integer standardDocId;     // 所属标准文档ID

        private List<CodeItemTreeNode> items;   // 树形结构的代码项

        /**
         * 代码项树形节点
         */
        @Data
        public static class CodeItemTreeNode {
            private Integer id;
            private String code;            // 代码值，如 "1"
            private String value;           // 含义，如 "卫生健康行政部门"
            private Integer parentId;
            private Integer sortOrder;
            private List<CodeItemTreeNode> children;   // 子节点列表
        }
    }
