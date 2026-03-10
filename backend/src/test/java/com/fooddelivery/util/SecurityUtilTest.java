package com.fooddelivery.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 安全工具类单元测试
 */
class SecurityUtilTest {

    private SecurityUtil securityUtil;

    @BeforeEach
    void setUp() {
        securityUtil = new SecurityUtil();
    }

    @Test
    @DisplayName("HTML 转义 - 防止 XSS")
    void escapeHtml_PreventXss() {
        String malicious = "<script>alert('xss')</script>";
        String escaped = securityUtil.escapeHtml(malicious);
        
        assertFalse(escaped.contains("<script>"));
        assertTrue(escaped.contains("&lt;script&gt;"));
    }

    @Test
    @DisplayName("HTML 转义 - 空值处理")
    void escapeHtml_NullInput() {
        assertNull(securityUtil.escapeHtml(null));
    }

    @Test
    @DisplayName("验证手机号 - 有效")
    void isValidPhone_Valid() {
        assertTrue(securityUtil.isValidPhone("13800138000"));
        assertTrue(securityUtil.isValidPhone("15912345678"));
        assertTrue(securityUtil.isValidPhone("18888888888"));
    }

    @Test
    @DisplayName("验证手机号 - 无效")
    void isValidPhone_Invalid() {
        assertFalse(securityUtil.isValidPhone("12345678901")); // 不是1开头的有效号段
        assertFalse(securityUtil.isValidPhone("1380013800")); // 位数不足
        assertFalse(securityUtil.isValidPhone("138001380001")); // 位数过多
        assertFalse(securityUtil.isValidPhone("abcdefghijk")); // 非数字
        assertFalse(securityUtil.isValidPhone(null));
        assertFalse(securityUtil.isValidPhone(""));
    }

    @Test
    @DisplayName("验证邮箱 - 有效")
    void isValidEmail_Valid() {
        assertTrue(securityUtil.isValidEmail("test@example.com"));
        assertTrue(securityUtil.isValidEmail("user.name@domain.org"));
        assertTrue(securityUtil.isValidEmail("user+tag@example.co.uk"));
    }

    @Test
    @DisplayName("验证邮箱 - 无效")
    void isValidEmail_Invalid() {
        assertFalse(securityUtil.isValidEmail("invalid"));
        assertFalse(securityUtil.isValidEmail("@example.com"));
        assertFalse(securityUtil.isValidEmail("test@"));
        assertFalse(securityUtil.isValidEmail(null));
        assertFalse(securityUtil.isValidEmail(""));
    }

    @Test
    @DisplayName("检测 SQL 注入 - 包含危险关键字")
    void containsSqlInjection_Detected() {
        assertTrue(securityUtil.containsSqlInjection("1; DROP TABLE users;"));
        assertTrue(securityUtil.containsSqlInjection("' OR '1'='1"));
        assertTrue(securityUtil.containsSqlInjection("SELECT * FROM users"));
        assertTrue(securityUtil.containsSqlInjection("1 UNION SELECT password FROM users"));
    }

    @Test
    @DisplayName("检测 SQL 注入 - 正常输入")
    void containsSqlInjection_Safe() {
        assertFalse(securityUtil.containsSqlInjection("正常的用户输入"));
        assertFalse(securityUtil.containsSqlInjection("Hello World"));
        assertFalse(securityUtil.containsSqlInjection("12345"));
        assertFalse(securityUtil.containsSqlInjection(null));
        assertFalse(securityUtil.containsSqlInjection(""));
    }

    @Test
    @DisplayName("清理用户输入")
    void sanitizeInput() {
        String input = "  <script>alert('xss')</script>  ";
        String sanitized = securityUtil.sanitizeInput(input);
        
        assertFalse(sanitized.contains("<script>"));
        assertEquals(sanitized, sanitized.trim());
    }

    @Test
    @DisplayName("手机号脱敏")
    void maskPhone() {
        assertEquals("138****5678", securityUtil.maskPhone("13812345678"));
        assertEquals("invalid", securityUtil.maskPhone("invalid"));
        assertNull(securityUtil.maskPhone(null));
    }

    @Test
    @DisplayName("身份证号脱敏")
    void maskIdCard() {
        assertEquals("110101********1234", securityUtil.maskIdCard("110101199001011234"));
        assertEquals("short", securityUtil.maskIdCard("short"));
        assertNull(securityUtil.maskIdCard(null));
    }

    @Test
    @DisplayName("邮箱脱敏")
    void maskEmail() {
        assertEquals("t***@example.com", securityUtil.maskEmail("test@example.com"));
        assertEquals("invalid", securityUtil.maskEmail("invalid"));
        assertNull(securityUtil.maskEmail(null));
    }
}
