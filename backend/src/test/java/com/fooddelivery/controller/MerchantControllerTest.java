package com.fooddelivery.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fooddelivery.common.Result;
import com.fooddelivery.controller.admin.MerchantController;
import com.fooddelivery.dto.MerchantDTO;
import com.fooddelivery.entity.Merchant;
import com.fooddelivery.service.MerchantService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * 商家控制器测试
 */
@WebMvcTest(MerchantController.class)
class MerchantControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private MerchantService merchantService;

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
    @DisplayName("获取商家详情 - 成功")
    void getById_Success() throws Exception {
        when(merchantService.getById(1L)).thenReturn(testMerchant);

        mockMvc.perform(get("/admin/merchant/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(1))
                .andExpect(jsonPath("$.data.name").value("测试商家"));
    }

    @Test
    @DisplayName("新增商家 - 成功")
    void add_Success() throws Exception {
        when(merchantService.save(any(Merchant.class))).thenReturn(true);

        mockMvc.perform(post("/admin/merchant")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testMerchantDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(1));
    }

    @Test
    @DisplayName("更新商家 - 成功")
    void update_Success() throws Exception {
        when(merchantService.updateById(any(Merchant.class))).thenReturn(true);

        mockMvc.perform(put("/admin/merchant")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testMerchantDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(1));
    }

    @Test
    @DisplayName("删除商家 - 成功")
    void delete_Success() throws Exception {
        when(merchantService.removeById(anyLong())).thenReturn(true);

        mockMvc.perform(delete("/admin/merchant/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(1));
    }
}
