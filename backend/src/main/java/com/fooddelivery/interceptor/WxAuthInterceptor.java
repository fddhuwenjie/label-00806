package com.fooddelivery.interceptor;

import com.fooddelivery.common.BaseContext;
import com.fooddelivery.common.Constants;
import com.fooddelivery.entity.User;
import com.fooddelivery.service.UserService;
import com.fooddelivery.util.JwtUtil;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;

@Slf4j
@Component
@RequiredArgsConstructor
public class WxAuthInterceptor implements HandlerInterceptor {

    private final JwtUtil jwtUtil;
    private final UserService userService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        String token = request.getHeader(Constants.TOKEN_HEADER);
        if (!StringUtils.hasText(token)) {
            response.setStatus(401);
            return false;
        }

        if (token.startsWith(Constants.TOKEN_PREFIX)) {
            token = token.substring(Constants.TOKEN_PREFIX.length());
        }

        try {
            Claims claims = jwtUtil.parseToken(token);
            Long userId = claims.get("userId", Long.class);
            
            // 检查用户是否被禁用
            User user = userService.getById(userId);
            if (user == null || user.getStatus() == Constants.STATUS_DISABLED) {
                log.warn("用户已被禁用或不存在: userId={}", userId);
                response.setStatus(403);
                return false;
            }
            
            BaseContext.setUserId(userId);
            return true;
        } catch (Exception e) {
            log.warn("Token解析失败: {}", e.getMessage());
            response.setStatus(401);
            return false;
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        BaseContext.clear();
    }
}
