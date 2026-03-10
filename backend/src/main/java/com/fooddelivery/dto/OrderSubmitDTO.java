package com.fooddelivery.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class OrderSubmitDTO {
    @NotNull(message = "商家ID不能为空")
    private Long merchantId;
    @NotNull(message = "地址ID不能为空")
    private Long addressId;
    private String remark;
}
