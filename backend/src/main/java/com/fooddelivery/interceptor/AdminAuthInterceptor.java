package com.fooddelivery.interceptor;

import com.fooddelivery.common.BaseContext;
import com.fooddelivery.common.Constants;
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
public class AdminAuthInterceptor implements HandlerInterceptor {

    private final JwtUtil jwtUtil;

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
            Long adminId = claims.get("adminId", Long.class);
            BaseContext.setAdminId(adminId);
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
