package com.fooddelivery.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.fooddelivery.dto.CartDTO;
import com.fooddelivery.entity.CartItem;
import com.fooddelivery.vo.CartVO;

import java.util.List;

public interface CartService extends IService<CartItem> {
    List<CartVO> listByUserId(Long userId);
    List<CartVO> listByUserIdAndMerchantId(Long userId, Long merchantId);
    void addCart(Long userId, CartDTO dto);
    void updateCart(Long userId, CartDTO dto);
    void deleteCart(Long userId, Long id);
    void clearCart(Long userId, Long merchantId);
}
