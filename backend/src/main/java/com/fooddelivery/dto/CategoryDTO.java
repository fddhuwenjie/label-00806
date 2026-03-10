package com.fooddelivery.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CategoryDTO {
    private Long id;
    @NotNull(message = "商家ID不能为空")
    private Long merchantId;
    @NotBlank(message = "分类名称不能为空")
    private String name;
    private Integer sortOrder;
    private Integer status;
}
