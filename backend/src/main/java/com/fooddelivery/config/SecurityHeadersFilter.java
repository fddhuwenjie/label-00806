package com.fooddelivery.config;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * 安全响应头过滤器
 * 添加安全相关的 HTTP 响应头
 */
@Component
@Order(1)
public class SecurityHeadersFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        
        // 防止点击劫持
        httpResponse.setHeader("X-Frame-Options", "DENY");
        
        // 防止 MIME 类型嗅探
        httpResponse.setHeader("X-Content-Type-Options", "nosniff");
        
        // XSS 保护
        httpResponse.setHeader("X-XSS-Protection", "1; mode=block");
        
        // 内容安全策略
        httpResponse.setHeader("Content-Security-Policy", 
            "default-src 'self'; script-src 'self' 'unsafe-inline'; style-src 'self' 'unsafe-inline'");
        
        // 引用策略
        httpResponse.setHeader("Referrer-Policy", "strict-origin-when-cross-origin");
        
        // 权限策略
        httpResponse.setHeader("Permissions-Policy", 
            "geolocation=(), microphone=(), camera=()");
        
        // 缓存控制（对于 API 响应）
        if (!httpResponse.containsHeader("Cache-Control")) {
            httpResponse.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");
        }
        
        chain.doFilter(request, response);
    }
}
