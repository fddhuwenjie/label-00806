package com.fooddelivery.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("merchant")
public class Merchant {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String name;
    private String logo;
    private String address;
    private String phone;
    private BigDecimal minPrice;
    private BigDecimal deliveryFee;
    private Integer status;
    private BigDecimal rating;
    private Integer monthlySales;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
