package com.fooddelivery.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class WxLoginDTO {
    @NotBlank(message = "code不能为空")
    private String code;
}
