package com.fooddelivery.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.fooddelivery.dto.CategoryDTO;
import com.fooddelivery.entity.Category;

import java.util.List;

public interface CategoryService extends IService<Category> {
    List<Category> listByMerchantId(Long merchantId);
    void saveCategory(CategoryDTO dto);
    void updateCategory(CategoryDTO dto);
}
