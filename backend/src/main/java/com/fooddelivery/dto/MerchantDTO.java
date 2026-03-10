package com.fooddelivery.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class MerchantDTO {
    private Long id;
    @NotBlank(message = "商家名称不能为空")
    private String name;
    private String logo;
    private String address;
    private String phone;
    @NotNull(message = "起送价不能为空")
    private BigDecimal minPrice;
    @NotNull(message = "配送费不能为空")
    private BigDecimal deliveryFee;
    private Integer status;
}
