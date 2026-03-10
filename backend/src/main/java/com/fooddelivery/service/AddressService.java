package com.fooddelivery.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.fooddelivery.dto.AddressDTO;
import com.fooddelivery.entity.Address;

import java.util.List;

public interface AddressService extends IService<Address> {
    List<Address> listByUserId(Long userId);
    Address getDefault(Long userId);
    void saveAddress(Long userId, AddressDTO dto);
    void updateAddress(Long userId, AddressDTO dto);
    void deleteAddress(Long userId, Long id);
}
