package com.fooddelivery.util;

import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * JWT 工具类单元测试
 */
class JwtUtilTest {

    private JwtUtil jwtUtil;

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil();
        // 设置测试用的密钥和过期时间
        ReflectionTestUtils.setField(jwtUtil, "secret", "testsecretkeyforsecuritypurposemustbe256bitslong");
        ReflectionTestUtils.setField(jwtUtil, "expiration", 86400000L);
    }

    @Test
    @DisplayName("生成 Token - 成功")
    void generateToken_Success() {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", 1L);
        claims.put("username", "admin");

        String token = jwtUtil.generateToken(claims);

        assertNotNull(token);
        assertTrue(token.length() > 0);
        assertTrue(token.split("\\.").length == 3); // JWT 格式: header.payload.signature
    }

    @Test
    @DisplayName("解析 Token - 成功")
    void parseToken_Success() {
        Long userId = 123L;
        String username = "testuser";
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        claims.put("username", username);

        String token = jwtUtil.generateToken(claims);
        Claims parsedClaims = jwtUtil.parseToken(token);

        assertEquals(userId.intValue(), parsedClaims.get("userId", Integer.class));
        assertEquals(username, parsedClaims.get("username", String.class));
    }

    @Test
    @DisplayName("验证 Token - 有效")
    void validateToken_Valid() {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", 1L);
        String token = jwtUtil.generateToken(claims);

        boolean isValid = jwtUtil.validateToken(token);

        assertTrue(isValid);
    }

    @Test
    @DisplayName("验证 Token - 无效格式")
    void validateToken_InvalidFormat() {
        String invalidToken = "invalid.token.format";

        boolean isValid = jwtUtil.validateToken(invalidToken);

        assertFalse(isValid);
    }

    @Test
    @DisplayName("验证 Token - 空值")
    void validateToken_Null() {
        boolean isValid = jwtUtil.validateToken(null);

        assertFalse(isValid);
    }

    @Test
    @DisplayName("验证 Token - 空字符串")
    void validateToken_Empty() {
        boolean isValid = jwtUtil.validateToken("");

        assertFalse(isValid);
    }

    @Test
    @DisplayName("Token 包含正确的过期时间")
    void token_ContainsExpiration() {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", 1L);

        String token = jwtUtil.generateToken(claims);
        Claims parsedClaims = jwtUtil.parseToken(token);

        assertNotNull(parsedClaims.getExpiration());
        assertTrue(parsedClaims.getExpiration().getTime() > System.currentTimeMillis());
    }

    @Test
    @DisplayName("Token 包含签发时间")
    void token_ContainsIssuedAt() {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", 1L);

        String token = jwtUtil.generateToken(claims);
        Claims parsedClaims = jwtUtil.parseToken(token);

        assertNotNull(parsedClaims.getIssuedAt());
        assertTrue(parsedClaims.getIssuedAt().getTime() <= System.currentTimeMillis());
    }
}
