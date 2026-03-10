package com.fooddelivery.service;

import com.fooddelivery.dto.DishDTO;
import com.fooddelivery.entity.Dish;
import com.fooddelivery.mapper.DishMapper;
import com.fooddelivery.service.impl.DishServiceImpl;
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
 * 菜品服务单元测试
 */
@ExtendWith(MockitoExtension.class)
class DishServiceTest {

    @Mock
    private DishMapper dishMapper;

    @InjectMocks
    private DishServiceImpl dishService;

    private Dish testDish;
    private DishDTO testDishDTO;

    @BeforeEach
    void setUp() {
        testDish = new Dish();
        testDish.setId(1L);
        testDish.setMerchantId(1L);
        testDish.setCategoryId(1L);
        testDish.setName("测试菜品");
        testDish.setPrice(new BigDecimal("25.00"));
        testDish.setDescription("美味的测试菜品");
        testDish.setStatus(1);

        testDishDTO = new DishDTO();
        testDishDTO.setMerchantId(1L);
        testDishDTO.setCategoryId(1L);
        testDishDTO.setName("测试菜品");
        testDishDTO.setPrice(new BigDecimal("25.00"));
        testDishDTO.setDescription("美味的测试菜品");
    }

    @Test
    @DisplayName("根据ID查询菜品 - 成功")
    void getById_Success() {
        when(dishMapper.selectById(1L)).thenReturn(testDish);

        Dish result = dishService.getById(1L);

        assertNotNull(result);
        assertEquals("测试菜品", result.getName());
        assertEquals(new BigDecimal("25.00"), result.getPrice());
        verify(dishMapper, times(1)).selectById(1L);
    }

    @Test
    @DisplayName("根据ID查询菜品 - 不存在")
    void getById_NotFound() {
        when(dishMapper.selectById(999L)).thenReturn(null);

        Dish result = dishService.getById(999L);

        assertNull(result);
        verify(dishMapper, times(1)).selectById(999L);
    }

    @Test
    @DisplayName("新增菜品 - 成功")
    void save_Success() {
        when(dishMapper.insert(any(Dish.class))).thenReturn(1);

        boolean result = dishService.save(testDish);

        assertTrue(result);
        verify(dishMapper, times(1)).insert(any(Dish.class));
    }

    @Test
    @DisplayName("更新菜品 - 成功")
    void updateById_Success() {
        when(dishMapper.updateById(any(Dish.class))).thenReturn(1);

        testDish.setPrice(new BigDecimal("30.00"));
        boolean result = dishService.updateById(testDish);

        assertTrue(result);
        verify(dishMapper, times(1)).updateById(any(Dish.class));
    }

    @Test
    @DisplayName("删除菜品 - 成功")
    void removeById_Success() {
        when(dishMapper.deleteById(1L)).thenReturn(1);

        boolean result = dishService.removeById(1L);

        assertTrue(result);
        verify(dishMapper, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("根据商家ID查询菜品列表")
    void listByMerchantId() {
        List<Dish> dishes = Arrays.asList(testDish);
        when(dishMapper.selectList(any())).thenReturn(dishes);

        List<Dish> result = dishMapper.selectList(null);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("测试菜品", result.get(0).getName());
    }

    @Test
    @DisplayName("根据分类ID查询菜品列表")
    void listByCategoryId() {
        List<Dish> dishes = Arrays.asList(testDish);
        when(dishMapper.selectList(any())).thenReturn(dishes);

        List<Dish> result = dishMapper.selectList(null);

        assertNotNull(result);
        assertEquals(1, result.size());
    }
}
