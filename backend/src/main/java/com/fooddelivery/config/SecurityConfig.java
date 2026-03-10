package com.fooddelivery.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * 安全配置类
 * 提供密码加密等安全相关的Bean
 */
@Configuration
public class SecurityConfig {

    /**
     * 密码加密器
     * 使用 BCrypt 算法进行密码加密
     * BCrypt 是一种自适应哈希函数，可以抵抗暴力破解
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }
}
