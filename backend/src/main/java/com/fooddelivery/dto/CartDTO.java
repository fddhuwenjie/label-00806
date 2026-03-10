package com.fooddelivery.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CartDTO {
    private Long id;
    private Long merchantId;
    private Long dishId;
    private Integer quantity;
}
