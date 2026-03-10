package com.fooddelivery.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fooddelivery.common.Constants;
import com.fooddelivery.entity.Order;
import com.fooddelivery.service.*;
import com.fooddelivery.vo.DashboardVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {

    private final OrderService orderService;
    private final UserService userService;
    private final MerchantService merchantService;

    @Override
    public DashboardVO getStatistics() {
        LocalDateTime todayStart = LocalDateTime.of(LocalDate.now(), LocalTime.MIN);
        LocalDateTime todayEnd = LocalDateTime.of(LocalDate.now(), LocalTime.MAX);

        // 今日订单
        long todayOrders = orderService.count(new LambdaQueryWrapper<Order>()
                .ge(Order::getCreateTime, todayStart)
                .le(Order::getCreateTime, todayEnd)
                .ne(Order::getStatus, Constants.ORDER_CANCELLED));

        // 总订单
        long totalOrders = orderService.count(new LambdaQueryWrapper<Order>()
                .ne(Order::getStatus, Constants.ORDER_CANCELLED));

        // 今日营业额
        List<Order> todayOrderList = orderService.list(new LambdaQueryWrapper<Order>()
                .ge(Order::getCreateTime, todayStart)
                .le(Order::getCreateTime, todayEnd)
                .eq(Order::getStatus, Constants.ORDER_COMPLETED));
        BigDecimal todayAmount = todayOrderList.stream()
                .map(Order::getActualAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // 总营业额
        List<Order> allCompletedOrders = orderService.list(new LambdaQueryWrapper<Order>()
                .eq(Order::getStatus, Constants.ORDER_COMPLETED));
        BigDecimal totalAmount = allCompletedOrders.stream()
                .map(Order::getActualAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // 用户数
        long totalUsers = userService.count();

        // 商家数
        long totalMerchants = merchantService.count();

        return DashboardVO.builder()
                .todayOrders(todayOrders)
                .totalOrders(totalOrders)
                .todayAmount(todayAmount)
                .totalAmount(totalAmount)
                .totalUsers(totalUsers)
                .totalMerchants(totalMerchants)
                .build();
    }
}
