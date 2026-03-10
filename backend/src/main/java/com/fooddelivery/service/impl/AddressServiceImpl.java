package com.fooddelivery.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fooddelivery.dto.AddressDTO;
import com.fooddelivery.entity.Address;
import com.fooddelivery.mapper.AddressMapper;
import com.fooddelivery.service.AddressService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
public class AddressServiceImpl extends ServiceImpl<AddressMapper, Address> implements AddressService {

    @Override
    public List<Address> listByUserId(Long userId) {
        return list(new LambdaQueryWrapper<Address>()
                .eq(Address::getUserId, userId)
                .orderByDesc(Address::getIsDefault)
                .orderByDesc(Address::getCreateTime));
    }

    @Override
    public Address getDefault(Long userId) {
        return getOne(new LambdaQueryWrapper<Address>()
                .eq(Address::getUserId, userId)
                .eq(Address::getIsDefault, 1));
    }

    @Override
    @Transactional
    public void saveAddress(Long userId, AddressDTO dto) {
        Address address = new Address();
        BeanUtil.copyProperties(dto, address);
        address.setUserId(userId);

        // 如果设为默认，先取消其他默认
        if (dto.getIsDefault() != null && dto.getIsDefault() == 1) {
            clearDefault(userId);
        }

        save(address);
        log.info("新增地址: userId={}", userId);
    }

    @Override
    @Transactional
    public void updateAddress(Long userId, AddressDTO dto) {
        Address address = new Address();
        BeanUtil.copyProperties(dto, address);

        // 如果设为默认，先取消其他默认
        if (dto.getIsDefault() != null && dto.getIsDefault() == 1) {
            clearDefault(userId);
        }

        updateById(address);
        log.info("更新地址: id={}", dto.getId());
    }

    @Override
    public void deleteAddress(Long userId, Long id) {
        remove(new LambdaQueryWrapper<Address>()
                .eq(Address::getId, id)
                .eq(Address::getUserId, userId));
        log.info("删除地址: id={}", id);
    }

    private void clearDefault(Long userId) {
        update(new LambdaUpdateWrapper<Address>()
                .eq(Address::getUserId, userId)
                .set(Address::getIsDefault, 0));
    }
}
