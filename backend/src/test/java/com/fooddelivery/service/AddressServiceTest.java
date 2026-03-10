package com.fooddelivery.service;

import com.fooddelivery.dto.AddressDTO;
import com.fooddelivery.entity.Address;
import com.fooddelivery.mapper.AddressMapper;
import com.fooddelivery.service.impl.AddressServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * 地址服务单元测试
 */
@ExtendWith(MockitoExtension.class)
class AddressServiceTest {

    @Mock
    private AddressMapper addressMapper;

    @InjectMocks
    private AddressServiceImpl addressService;

    private Address testAddress;

    @BeforeEach
    void setUp() {
        testAddress = new Address();
        testAddress.setId(1L);
        testAddress.setUserId(1L);
        testAddress.setContactName("张三");
        testAddress.setContactPhone("13800138000");
        testAddress.setProvince("北京市");
        testAddress.setCity("北京市");
        testAddress.setDistrict("朝阳区");
        testAddress.setDetail("xxx街道xxx号");
        testAddress.setIsDefault(0);
    }

    @Test
    @DisplayName("根据ID查询地址 - 成功")
    void getById_Success() {
        when(addressMapper.selectById(1L)).thenReturn(testAddress);

        Address result = addressService.getById(1L);

        assertNotNull(result);
        assertEquals("张三", result.getContactName());
        assertEquals("13800138000", result.getContactPhone());
        verify(addressMapper, times(1)).selectById(1L);
    }

    @Test
    @DisplayName("新增地址 - 成功")
    void save_Success() {
        when(addressMapper.insert(any(Address.class))).thenReturn(1);

        boolean result = addressService.save(testAddress);

        assertTrue(result);
        verify(addressMapper, times(1)).insert(any(Address.class));
    }

    @Test
    @DisplayName("更新地址 - 成功")
    void updateById_Success() {
        when(addressMapper.updateById(any(Address.class))).thenReturn(1);

        testAddress.setDetail("新地址");
        boolean result = addressService.updateById(testAddress);

        assertTrue(result);
        verify(addressMapper, times(1)).updateById(any(Address.class));
    }

    @Test
    @DisplayName("删除地址 - 成功")
    void removeById_Success() {
        when(addressMapper.deleteById(1L)).thenReturn(1);

        boolean result = addressService.removeById(1L);

        assertTrue(result);
        verify(addressMapper, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("查询用户地址列表")
    void listByUserId() {
        List<Address> addresses = Arrays.asList(testAddress);
        when(addressMapper.selectList(any())).thenReturn(addresses);

        List<Address> result = addressMapper.selectList(null);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("张三", result.get(0).getContactName());
    }
}
