package com.fooddelivery.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

/**
 * Web 安全配置
 * 配置 CORS、安全头等
 */
@Configuration
public class WebSecurityConfig {

    @Value("${cors.allowed-origins:*}")
    private String allowedOrigins;

    @Value("${cors.max-age:3600}")
    private Long maxAge;

    /**
     * CORS 配置
     * 允许跨域请求
     * 生产环境应通过配置文件限制具体域名
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        
        // 允许的源（生产环境应限制为具体域名）
        // 通过配置文件 cors.allowed-origins 设置，多个域名用逗号分隔
        if ("*".equals(allowedOrigins)) {
            configuration.setAllowedOriginPatterns(List.of("*"));
        } else {
            configuration.setAllowedOrigins(Arrays.asList(allowedOrigins.split(",")));
        }
        
        // 允许的 HTTP 方法
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
        
        // 允许的请求头
        configuration.setAllowedHeaders(Arrays.asList(
            "Authorization",
            "Content-Type",
            "X-Requested-With",
            "Accept",
            "Origin",
            "X-CSRF-Token",
            "X-Request-Id"
        ));
        
        // 暴露的响应头
        configuration.setExposedHeaders(Arrays.asList(
            "Authorization",
            "Content-Disposition",
            "X-Request-Id"
        ));
        
        // 允许携带凭证
        configuration.setAllowCredentials(true);
        
        // 预检请求缓存时间（秒）
        configuration.setMaxAge(maxAge);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
