package com.fooddelivery.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fooddelivery.entity.OrderItem;
import com.fooddelivery.mapper.OrderItemMapper;
import com.fooddelivery.service.OrderItemService;
import org.springframework.stereotype.Service;

@Service
public class OrderItemServiceImpl extends ServiceImpl<OrderItemMapper, OrderItem> implements OrderItemService {
}
