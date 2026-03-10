package com.fooddelivery.common;

public class Constants {
    // 状态
    public static final int STATUS_DISABLED = 0;
    public static final int STATUS_ENABLED = 1;

    // 订单状态
    public static final int ORDER_PENDING_PAY = 0;      // 待支付
    public static final int ORDER_PENDING_ACCEPT = 1;   // 待接单
    public static final int ORDER_DELIVERING = 2;       // 配送中
    public static final int ORDER_COMPLETED = 3;        // 已完成
    public static final int ORDER_CANCELLED = 4;        // 已取消

    // 管理员角色
    public static final int ROLE_ADMIN = 1;
    public static final int ROLE_SUPER_ADMIN = 2;

    // Token 请求头
    public static final String TOKEN_HEADER = "Authorization";
    public static final String TOKEN_PREFIX = "Bearer ";
}
