package com.fooddelivery.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fooddelivery.dto.MerchantDTO;
import com.fooddelivery.dto.PageQueryDTO;
import com.fooddelivery.entity.Merchant;
import com.fooddelivery.mapper.MerchantMapper;
import com.fooddelivery.service.impl.MerchantServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * 商家服务单元测试
 */
@ExtendWith(MockitoExtension.class)
class MerchantServiceTest {

    @Mock
    private MerchantMapper merchantMapper;

    @InjectMocks
    private MerchantServiceImpl merchantService;

    private Merchant testMerchant;
    private MerchantDTO testMerchantDTO;

    @BeforeEach
    void setUp() {
        testMerchant = new Merchant();
        testMerchant.setId(1L);
        testMerchant.setName("测试商家");
        testMerchant.setAddress("测试地址");
        testMerchant.setPhone("13800138000");
        testMerchant.setMinPrice(new BigDecimal("20.00"));
        testMerchant.setDeliveryFee(new BigDecimal("5.00"));
        testMerchant.setStatus(1);

        testMerchantDTO = new MerchantDTO();
        testMerchantDTO.setName("测试商家");
        testMerchantDTO.setAddress("测试地址");
        testMerchantDTO.setPhone("13800138000");
        testMerchantDTO.setMinPrice(new BigDecimal("20.00"));
        testMerchantDTO.setDeliveryFee(new BigDecimal("5.00"));
    }

    @Test
    @DisplayName("根据ID查询商家 - 成功")
    void getById_Success() {
        when(merchantMapper.selectById(1L)).thenReturn(testMerchant);

        Merchant result = merchantService.getById(1L);

        assertNotNull(result);
        assertEquals("测试商家", result.getName());
        verify(merchantMapper, times(1)).selectById(1L);
    }

    @Test
    @DisplayName("根据ID查询商家 - 不存在")
    void getById_NotFound() {
        when(merchantMapper.selectById(999L)).thenReturn(null);

        Merchant result = merchantService.getById(999L);

        assertNull(result);
        verify(merchantMapper, times(1)).selectById(999L);
    }

    @Test
    @DisplayName("新增商家 - 成功")
    void save_Success() {
        when(merchantMapper.insert(any(Merchant.class))).thenReturn(1);

        boolean result = merchantService.save(testMerchant);

        assertTrue(result);
        verify(merchantMapper, times(1)).insert(any(Merchant.class));
    }

    @Test
    @DisplayName("更新商家 - 成功")
    void updateById_Success() {
        when(merchantMapper.updateById(any(Merchant.class))).thenReturn(1);

        boolean result = merchantService.updateById(testMerchant);

        assertTrue(result);
        verify(merchantMapper, times(1)).updateById(any(Merchant.class));
    }

    @Test
    @DisplayName("删除商家 - 成功")
    void removeById_Success() {
        when(merchantMapper.deleteById(1L)).thenReturn(1);

        boolean result = merchantService.removeById(1L);

        assertTrue(result);
        verify(merchantMapper, times(1)).deleteById(1L);
    }
}
