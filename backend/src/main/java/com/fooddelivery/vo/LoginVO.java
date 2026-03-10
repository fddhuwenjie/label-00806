package com.fooddelivery.vo;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LoginVO {
    private Long id;
    private String username;
    private String realName;
    private String token;
}
