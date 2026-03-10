package com.fooddelivery.vo;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;

@Data
@Builder
public class DashboardVO {
    private Long todayOrders;
    private Long totalOrders;
    private BigDecimal todayAmount;
    private BigDecimal totalAmount;
    private Long totalUsers;
    private Long totalMerchants;
}
