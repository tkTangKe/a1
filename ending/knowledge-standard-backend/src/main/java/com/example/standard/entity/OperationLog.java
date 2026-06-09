package com.example.standard.entity;
//
//import com.baomidou.mybatisplus.annotation.IdType;
//import com.baomidou.mybatisplus.annotation.TableId;
//import com.baomidou.mybatisplus.annotation.TableName;
//import lombok.Data;
//import java.time.LocalDateTime;
//
//@Data
//@TableName("operation_log")
//public class OperationLog {
//    @TableId(type = IdType.AUTO)
//    private Integer id;
//    private String username;
//    private String operation;
//    private String targetType;
//    private Integer targetId;
//    private String detail;
//    private String ip;
//    private LocalDateTime createTime;
//}
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDateTime;

@TableName("operation_log")   // 替换为你的实际表名
public class OperationLog {
    @TableId(type = IdType.AUTO)
    private Long id;

    private String username;
    private String operation;

    @TableField("target_type")
    private String targetType;

    @TableField("target_id")
    private String targetId;

    private String detail;
    private String ip;

    @TableField("create_time")
    private LocalDateTime createTime;

    // 必须添加所有 getter 和 setter
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getOperation() { return operation; }
    public void setOperation(String operation) { this.operation = operation; }
    public String getTargetType() { return targetType; }
    public void setTargetType(String targetType) { this.targetType = targetType; }
    public String getTargetId() { return targetId; }
    public void setTargetId(String targetId) { this.targetId = targetId; }
    public String getDetail() { return detail; }
    public void setDetail(String detail) { this.detail = detail; }
    public String getIp() { return ip; }
    public void setIp(String ip) { this.ip = ip; }
    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }
}