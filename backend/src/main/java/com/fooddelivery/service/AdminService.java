package com.fooddelivery.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.fooddelivery.dto.LoginDTO;
import com.fooddelivery.entity.Admin;
import com.fooddelivery.vo.LoginVO;

public interface AdminService extends IService<Admin> {
    LoginVO login(LoginDTO dto);
    Admin getInfo(Long adminId);
}
