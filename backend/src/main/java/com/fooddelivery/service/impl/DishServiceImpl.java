package com.fooddelivery.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fooddelivery.common.Constants;
import com.fooddelivery.dto.DishDTO;
import com.fooddelivery.dto.PageQueryDTO;
import com.fooddelivery.entity.Dish;
import com.fooddelivery.mapper.DishMapper;
import com.fooddelivery.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Slf4j
@Service
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService {

    @Override
    public IPage<Dish> pageQuery(PageQueryDTO dto) {
        Page<Dish> page = new Page<>(dto.getPage(), dto.getPageSize());
        LambdaQueryWrapper<Dish> wrapper = new LambdaQueryWrapper<>();

        if (dto.getMerchantId() != null) {
            wrapper.eq(Dish::getMerchantId, dto.getMerchantId());
        }
        if (dto.getCategoryId() != null) {
            wrapper.eq(Dish::getCategoryId, dto.getCategoryId());
        }
        if (StringUtils.hasText(dto.getKeyword())) {
            wrapper.like(Dish::getName, dto.getKeyword());
        }
        if (dto.getStatus() != null) {
            wrapper.eq(Dish::getStatus, dto.getStatus());
        }

        wrapper.orderByDesc(Dish::getCreateTime);
        return page(page, wrapper);
    }

    @Override
    public void saveDish(DishDTO dto) {
        Dish dish = new Dish();
        BeanUtil.copyProperties(dto, dish);
        dish.setStatus(Constants.STATUS_ENABLED);
        dish.setSales(0);
        save(dish);
        log.info("新增菜品: {}", dish.getName());
    }

    @Override
    public void updateDish(DishDTO dto) {
        Dish dish = new Dish();
        BeanUtil.copyProperties(dto, dish);
        updateById(dish);
        log.info("更新菜品: id={}", dto.getId());
    }

    @Override
    public List<Dish> listByMerchantId(Long merchantId) {
        return list(new LambdaQueryWrapper<Dish>()
                .eq(Dish::getMerchantId, merchantId)
                .eq(Dish::getStatus, Constants.STATUS_ENABLED)
                .orderByDesc(Dish::getSales));
    }
}
