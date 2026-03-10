package com.fooddelivery.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.fooddelivery.dto.DishDTO;
import com.fooddelivery.dto.PageQueryDTO;
import com.fooddelivery.entity.Dish;

import java.util.List;

public interface DishService extends IService<Dish> {
    IPage<Dish> pageQuery(PageQueryDTO dto);
    void saveDish(DishDTO dto);
    void updateDish(DishDTO dto);
    List<Dish> listByMerchantId(Long merchantId);
}
