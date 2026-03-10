package com.fooddelivery.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.fooddelivery.dto.OrderSubmitDTO;
import com.fooddelivery.dto.PageQueryDTO;
import com.fooddelivery.entity.Order;
import com.fooddelivery.vo.OrderVO;

public interface OrderService extends IService<Order> {
    OrderVO submitOrder(Long userId, OrderSubmitDTO dto);
    IPage<OrderVO> pageQuery(PageQueryDTO dto);
    IPage<OrderVO> pageQueryForUser(Long userId, Integer status, Integer page, Integer pageSize);
    OrderVO getDetail(Long id);
    void updateStatus(Long id, Integer status);
    void cancelOrder(Long userId, Long id);
    void confirmOrder(Long userId, Long id);
    void payOrder(Long userId, Long id);
    void deleteOrder(Long userId, Long id);
    void autoCancelExpiredOrders();
}
