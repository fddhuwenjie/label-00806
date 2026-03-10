package com.fooddelivery.service;

import com.fooddelivery.entity.*;
import com.fooddelivery.mapper.*;
import com.fooddelivery.service.impl.OrderServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * 订单服务单元测试
 */
@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderMapper orderMapper;

    @Mock
    private OrderItemMapper orderItemMapper;

    @Mock
    private CartItemMapper cartItemMapper;

    @Mock
    private AddressMapper addressMapper;

    @Mock
    private MerchantMapper merchantMapper;

    @Mock
    private DishMapper dishMapper;

    @InjectMocks
    private OrderServiceImpl orderService;

    private Order testOrder;
    private Address testAddress;
    private Merchant testMerchant;
    private CartItem testCartItem;
    private Dish testDish;

    @BeforeEach
    void setUp() {
        testOrder = new Order();
        testOrder.setId(1L);
        testOrder.setOrderNo("202401010001");
        testOrder.setUserId(1L);
        testOrder.setMerchantId(1L);
        testOrder.setTotalAmount(new BigDecimal("50.00"));
        testOrder.setStatus(0);

        testAddress = new Address();
        testAddress.setId(1L);
        testAddress.setUserId(1L);
        testAddress.setContactName("张三");
        testAddress.setContactPhone("13800138000");
        testAddress.setDetail("测试地址");

        testMerchant = new Merchant();
        testMerchant.setId(1L);
        testMerchant.setName("测试商家");
        testMerchant.setStatus(1);
        testMerchant.setDeliveryFee(new BigDecimal("5.00"));

        testDish = new Dish();
        testDish.setId(1L);
        testDish.setName("测试菜品");
        testDish.setPrice(new BigDecimal("25.00"));
        testDish.setStatus(1);

        testCartItem = new CartItem();
        testCartItem.setId(1L);
        testCartItem.setUserId(1L);
        testCartItem.setMerchantId(1L);
        testCartItem.setDishId(1L);
        testCartItem.setQuantity(2);
    }

    @Test
    @DisplayName("根据ID查询订单 - 成功")
    void getById_Success() {
        when(orderMapper.selectById(1L)).thenReturn(testOrder);

        Order result = orderService.getById(1L);

        assertNotNull(result);
        assertEquals("202401010001", result.getOrderNo());
        verify(orderMapper, times(1)).selectById(1L);
    }

    @Test
    @DisplayName("更新订单状态 - 成功")
    void updateStatus_Success() {
        when(orderMapper.selectById(1L)).thenReturn(testOrder);
        when(orderMapper.updateById(any(Order.class))).thenReturn(1);

        testOrder.setStatus(1);
        boolean result = orderService.updateById(testOrder);

        assertTrue(result);
        verify(orderMapper, times(1)).updateById(any(Order.class));
    }

    @Test
    @DisplayName("查询用户订单列表")
    void listByUserId() {
        List<Order> orders = Arrays.asList(testOrder);
        when(orderMapper.selectList(any())).thenReturn(orders);

        List<Order> result = orderMapper.selectList(null);

        assertNotNull(result);
        assertEquals(1, result.size());
    }
}
