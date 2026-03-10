package com.fooddelivery.vo;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class WxLoginVO {
    private Long id;
    private String openid;
    private String token;
}
