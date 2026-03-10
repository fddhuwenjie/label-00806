package com.fooddelivery.vo;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class CartVO {
    private Long id;
    private Long merchantId;
    private String merchantName;
    private Long dishId;
    private String dishName;
    private String dishImage;
    private BigDecimal dishPrice;
    private Integer quantity;
}
