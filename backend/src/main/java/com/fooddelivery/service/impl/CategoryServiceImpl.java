package com.fooddelivery.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fooddelivery.common.Constants;
import com.fooddelivery.dto.CategoryDTO;
import com.fooddelivery.entity.Category;
import com.fooddelivery.mapper.CategoryMapper;
import com.fooddelivery.service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {

    @Override
    public List<Category> listByMerchantId(Long merchantId) {
        return list(new LambdaQueryWrapper<Category>()
                .eq(Category::getMerchantId, merchantId)
                .orderByAsc(Category::getSortOrder));
    }

    @Override
    public void saveCategory(CategoryDTO dto) {
        Category category = new Category();
        BeanUtil.copyProperties(dto, category);
        category.setStatus(Constants.STATUS_ENABLED);
        save(category);
        log.info("新增分类: {}", category.getName());
    }

    @Override
    public void updateCategory(CategoryDTO dto) {
        Category category = new Category();
        BeanUtil.copyProperties(dto, category);
        updateById(category);
        log.info("更新分类: id={}", dto.getId());
    }
}
