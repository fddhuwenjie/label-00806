package com.fooddelivery.util;

import org.springframework.stereotype.Component;
import org.springframework.web.util.HtmlUtils;

import java.util.regex.Pattern;

/**
 * 安全工具类
 * 提供输入验证、XSS 防护等安全相关功能
 */
@Component
public class SecurityUtil {

    // 手机号正则
    private static final Pattern PHONE_PATTERN = Pattern.compile("^1[3-9]\\d{9}$");
    
    // 邮箱正则
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
        "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$"
    );
    
    // SQL 注入关键字
    private static final Pattern SQL_INJECTION_PATTERN = Pattern.compile(
        "(?i)(select|insert|update|delete|drop|truncate|exec|execute|union|--|;)",
        Pattern.CASE_INSENSITIVE
    );

    /**
     * HTML 转义，防止 XSS 攻击
     */
    public String escapeHtml(String input) {
        if (input == null) {
            return null;
        }
        return HtmlUtils.htmlEscape(input);
    }

    /**
     * 验证手机号格式
     */
    public boolean isValidPhone(String phone) {
        if (phone == null || phone.isEmpty()) {
            return false;
        }
        return PHONE_PATTERN.matcher(phone).matches();
    }

    /**
     * 验证邮箱格式
     */
    public boolean isValidEmail(String email) {
        if (email == null || email.isEmpty()) {
            return false;
        }
        return EMAIL_PATTERN.matcher(email).matches();
    }

    /**
     * 检测是否包含 SQL 注入关键字
     */
    public boolean containsSqlInjection(String input) {
        if (input == null || input.isEmpty()) {
            return false;
        }
        return SQL_INJECTION_PATTERN.matcher(input).find();
    }

    /**
     * 清理用户输入
     * 移除潜在的危险字符
     */
    public String sanitizeInput(String input) {
        if (input == null) {
            return null;
        }
        // 移除控制字符
        String sanitized = input.replaceAll("[\\x00-\\x1F\\x7F]", "");
        // HTML 转义
        sanitized = escapeHtml(sanitized);
        return sanitized.trim();
    }

    /**
     * 脱敏手机号
     * 13812345678 -> 138****5678
     */
    public String maskPhone(String phone) {
        if (phone == null || phone.length() != 11) {
            return phone;
        }
        return phone.substring(0, 3) + "****" + phone.substring(7);
    }

    /**
     * 脱敏身份证号
     * 110101199001011234 -> 110101********1234
     */
    public String maskIdCard(String idCard) {
        if (idCard == null || idCard.length() < 15) {
            return idCard;
        }
        return idCard.substring(0, 6) + "********" + idCard.substring(idCard.length() - 4);
    }

    /**
     * 脱敏邮箱
     * test@example.com -> t***@example.com
     */
    public String maskEmail(String email) {
        if (email == null || !email.contains("@")) {
            return email;
        }
        int atIndex = email.indexOf("@");
        if (atIndex <= 1) {
            return email;
        }
        return email.charAt(0) + "***" + email.substring(atIndex);
    }
}
