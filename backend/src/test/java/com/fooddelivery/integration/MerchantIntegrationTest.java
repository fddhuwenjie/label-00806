package com.fooddelivery.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fooddelivery.dto.MerchantDTO;
import com.fooddelivery.entity.Merchant;
import com.fooddelivery.mapper.MerchantMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * 商家模块集成测试
 * 使用 H2 内存数据库进行测试
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class MerchantIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MerchantMapper merchantMapper;

    private Merchant testMerchant;

    @BeforeEach
    void setUp() {
        testMerchant = new Merchant();
        testMerchant.setName("集成测试商家");
        testMerchant.setAddress("测试地址");
        testMerchant.setPhone("13800138000");
        testMerchant.setMinPrice(new BigDecimal("20.00"));
        testMerchant.setDeliveryFee(new BigDecimal("5.00"));
        testMerchant.setStatus(1);
    }

    @Test
    @DisplayName("集成测试 - 商家CRUD完整流程")
    void testMerchantCrudFlow() throws Exception {
        // 1. 创建商家
        MerchantDTO createDTO = new MerchantDTO();
        createDTO.setName("新商家");
        createDTO.setAddress("新地址");
        createDTO.setPhone("13900139000");
        createDTO.setMinPrice(new BigDecimal("15.00"));
        createDTO.setDeliveryFee(new BigDecimal("3.00"));

        mockMvc.perform(post("/admin/merchant")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(1));

        // 2. 查询商家列表
        mockMvc.perform(get("/admin/merchant/page")
                        .param("page", "1")
                        .param("pageSize", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(1));
    }

    @Test
    @DisplayName("集成测试 - 商家状态变更")
    void testMerchantStatusChange() throws Exception {
        // 先插入测试数据
        merchantMapper.insert(testMerchant);

        // 禁用商家
        mockMvc.perform(put("/admin/merchant/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"id\":" + testMerchant.getId() + ",\"status\":0}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(1));

        // 验证状态已变更
        Merchant updated = merchantMapper.selectById(testMerchant.getId());
        assert updated.getStatus() == 0;
    }
}
