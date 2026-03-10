package com.fooddelivery.dto;

import lombok.Data;

@Data
public class PageQueryDTO {
    private Integer page = 1;
    private Integer pageSize = 10;
    private String keyword;
    private Long merchantId;
    private Long categoryId;
    private Integer status;
}
