package com.fooddelivery.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("operation_log")
public class OperationLog {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long adminId;
    private String adminName;
    private String module;
    private String operation;
    private String method;
    private String params;
    private String ip;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
