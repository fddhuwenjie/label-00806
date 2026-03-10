package com.fooddelivery.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.fooddelivery.dto.PageQueryDTO;
import com.fooddelivery.dto.WxLoginDTO;
import com.fooddelivery.entity.User;
import com.fooddelivery.vo.WxLoginVO;

public interface UserService extends IService<User> {
    WxLoginVO wxLogin(WxLoginDTO dto);
    User getInfo(Long userId);
    IPage<User> pageQuery(PageQueryDTO dto);
}
