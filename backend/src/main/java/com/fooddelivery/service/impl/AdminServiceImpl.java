package com.fooddelivery.service.impl;

import cn.hutool.crypto.digest.BCrypt;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fooddelivery.common.BusinessException;
import com.fooddelivery.common.Constants;
import com.fooddelivery.dto.LoginDTO;
import com.fooddelivery.entity.Admin;
import com.fooddelivery.mapper.AdminMapper;
import com.fooddelivery.service.AdminService;
import com.fooddelivery.util.JwtUtil;
import com.fooddelivery.vo.LoginVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminServiceImpl extends ServiceImpl<AdminMapper, Admin> implements AdminService {

    private final JwtUtil jwtUtil;

    @Override
    public LoginVO login(LoginDTO dto) {
        Admin admin = getOne(new LambdaQueryWrapper<Admin>()
                .eq(Admin::getUsername, dto.getUsername()));

        if (admin == null) {
            throw new BusinessException("用户名或密码错误");
        }

        // 密码验证：使用BCrypt校验
        boolean passwordMatch = false;
        try {
            passwordMatch = BCrypt.checkpw(dto.getPassword(), admin.getPassword());
        } catch (Exception e) {
            log.error("BCrypt校验异常: {}", e.getMessage());
            throw new BusinessException("系统错误，请稍后重试");
        }
        if (!passwordMatch) {
            throw new BusinessException("用户名或密码错误");
        }

        if (admin.getStatus() == Constants.STATUS_DISABLED) {
            throw new BusinessException("账号已被禁用");
        }

        Map<String, Object> claims = new HashMap<>();
        claims.put("adminId", admin.getId());
        claims.put("username", admin.getUsername());
        String token = jwtUtil.generateToken(claims);

        log.info("管理员登录成功: {}", admin.getUsername());

        return LoginVO.builder()
                .id(admin.getId())
                .username(admin.getUsername())
                .realName(admin.getRealName())
                .token(token)
                .build();
    }

    @Override
    public Admin getInfo(Long adminId) {
        Admin admin = getById(adminId);
        if (admin != null) {
            admin.setPassword(null);
        }
        return admin;
    }
}
