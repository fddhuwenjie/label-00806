package com.fooddelivery.config;

import com.fooddelivery.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@Slf4j
@Configuration
@EnableScheduling
@RequiredArgsConstructor
public class ScheduleConfig {

    private final OrderService orderService;

    /**
     * 每分钟检查一次超时未支付的订单
     */
    @Scheduled(fixedRate = 60000)
    public void autoCancelExpiredOrders() {
        try {
            orderService.autoCancelExpiredOrders();
        } catch (Exception e) {
            log.error("自动取消超时订单失败", e);
        }
    }
}
