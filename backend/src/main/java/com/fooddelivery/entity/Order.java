package com.fooddelivery.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("`order`")
public class Order {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String orderNo;
    private Long userId;
    private Long merchantId;
    private Long addressId;
    private String addressSnapshot;
    private BigDecimal totalAmount;
    private BigDecimal deliveryFee;
    private BigDecimal actualAmount;
    private Integer status;
    private String remark;
    private Integer userDeleted;  // 用户侧删除标记: 0-未删除 1-已删除
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    private LocalDateTime payTime;
    private LocalDateTime deliverTime;
    private LocalDateTime completeTime;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
