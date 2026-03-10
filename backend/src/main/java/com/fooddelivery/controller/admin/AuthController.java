package com.fooddelivery.controller.admin;

import com.fooddelivery.common.BaseContext;
import com.fooddelivery.common.Result;
import com.fooddelivery.dto.LoginDTO;
import com.fooddelivery.entity.Admin;
import com.fooddelivery.service.AdminService;
import com.fooddelivery.vo.LoginVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AdminService adminService;

    @PostMapping("/login")
    public Result<LoginVO> login(@Valid @RequestBody LoginDTO dto) {
        return Result.success(adminService.login(dto));
    }

    @PostMapping("/logout")
    public Result<Void> logout() {
        return Result.success();
    }

    @GetMapping("/info")
    public Result<Admin> info() {
        Long adminId = BaseContext.getAdminId();
        return Result.success(adminService.getInfo(adminId));
    }
}
