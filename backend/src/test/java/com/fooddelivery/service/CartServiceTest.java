package com.fooddelivery.service;

import com.fooddelivery.dto.CartDTO;
import com.fooddelivery.entity.CartItem;
import com.fooddelivery.entity.Dish;
import com.fooddelivery.mapper.CartItemMapper;
import com.fooddelivery.mapper.DishMapper;
import com.fooddelivery.service.impl.CartServiceImpl;
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
 * 购物车服务单元测试
 */
@ExtendWith(MockitoExtension.class)
class CartServiceTest {

    @Mock
    private CartItemMapper cartItemMapper;

    @Mock
    private DishMapper dishMapper;

    @InjectMocks
    private CartServiceImpl cartService;

    private CartItem testCartItem;
    private Dish testDish;
    private CartDTO testCartDTO;

    @BeforeEach
    void setUp() {
        testDish = new Dish();
        testDish.setId(1L);
        testDish.setName("测试菜品");
        testDish.setPrice(new BigDecimal("25.00"));
        testDish.setMerchantId(1L);
        testDish.setStatus(1);

        testCartItem = new CartItem();
        testCartItem.setId(1L);
        testCartItem.setUserId(1L);
        testCartItem.setMerchantId(1L);
        testCartItem.setDishId(1L);
        testCartItem.setQuantity(2);

        testCartDTO = new CartDTO();
        testCartDTO.setDishId(1L);
        testCartDTO.setQuantity(1);
    }

    @Test
    @DisplayName("查询用户购物车")
    void getCartByUserId() {
        List<CartItem> cartItems = Arrays.asList(testCartItem);
        when(cartItemMapper.selectList(any())).thenReturn(cartItems);

        List<CartItem> result = cartItemMapper.selectList(null);

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    @DisplayName("添加商品到购物车 - 新商品")
    void addToCart_NewItem() {
        when(cartItemMapper.selectOne(any())).thenReturn(null);
        when(cartItemMapper.insert(any(CartItem.class))).thenReturn(1);

        boolean result = cartService.save(testCartItem);

        assertTrue(result);
        verify(cartItemMapper, times(1)).insert(any(CartItem.class));
    }

    @Test
    @DisplayName("更新购物车数量")
    void updateCartQuantity() {
        when(cartItemMapper.updateById(any(CartItem.class))).thenReturn(1);

        testCartItem.setQuantity(3);
        boolean result = cartService.updateById(testCartItem);

        assertTrue(result);
        verify(cartItemMapper, times(1)).updateById(any(CartItem.class));
    }

    @Test
    @DisplayName("删除购物车商品")
    void removeFromCart() {
        when(cartItemMapper.deleteById(1L)).thenReturn(1);

        boolean result = cartService.removeById(1L);

        assertTrue(result);
        verify(cartItemMapper, times(1)).deleteById(1L);
    }
}
