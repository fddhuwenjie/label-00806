package com.fooddelivery.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fooddelivery.common.Constants;
import com.fooddelivery.dto.MerchantDTO;
import com.fooddelivery.dto.PageQueryDTO;
import com.fooddelivery.entity.Category;
import com.fooddelivery.entity.Dish;
import com.fooddelivery.entity.Merchant;
import com.fooddelivery.mapper.MerchantMapper;
import com.fooddelivery.service.CategoryService;
import com.fooddelivery.service.DishService;
import com.fooddelivery.service.MerchantService;
import com.fooddelivery.vo.MerchantDetailVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class MerchantServiceImpl extends ServiceImpl<MerchantMapper, Merchant> implements MerchantService {

    private final CategoryService categoryService;
    private final DishService dishService;

    @Override
    public IPage<Merchant> pageQuery(PageQueryDTO dto) {
        Page<Merchant> page = new Page<>(dto.getPage(), dto.getPageSize());
        LambdaQueryWrapper<Merchant> wrapper = new LambdaQueryWrapper<>();

        if (StringUtils.hasText(dto.getKeyword())) {
            wrapper.like(Merchant::getName, dto.getKeyword());
        }
        if (dto.getStatus() != null) {
            wrapper.eq(Merchant::getStatus, dto.getStatus());
        }

        wrapper.orderByDesc(Merchant::getCreateTime);
        return page(page, wrapper);
    }

    @Override
    public void saveMerchant(MerchantDTO dto) {
        Merchant merchant = new Merchant();
        BeanUtil.copyProperties(dto, merchant);
        merchant.setStatus(Constants.STATUS_ENABLED);
        save(merchant);
        log.info("新增商家: {}", merchant.getName());
    }

    @Override
    public void updateMerchant(MerchantDTO dto) {
        Merchant merchant = new Merchant();
        BeanUtil.copyProperties(dto, merchant);
        updateById(merchant);
        log.info("更新商家: id={}", dto.getId());
    }

    @Override
    public List<Merchant> listForWx(String keyword) {
        LambdaQueryWrapper<Merchant> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Merchant::getStatus, Constants.STATUS_ENABLED);

        if (StringUtils.hasText(keyword)) {
            wrapper.like(Merchant::getName, keyword);
        }

        wrapper.orderByDesc(Merchant::getMonthlySales);
        return list(wrapper);
    }

    @Override
    public MerchantDetailVO getDetailForWx(Long id) {
        Merchant merchant = getById(id);
        if (merchant == null) {
            return null;
        }

        MerchantDetailVO vo = new MerchantDetailVO();
        vo.setMerchant(merchant);

        // 获取分类及菜品
        List<Category> categories = categoryService.list(new LambdaQueryWrapper<Category>()
                .eq(Category::getMerchantId, id)
                .eq(Category::getStatus, Constants.STATUS_ENABLED)
                .orderByAsc(Category::getSortOrder));

        List<MerchantDetailVO.CategoryWithDishes> categoryList = new ArrayList<>();
        for (Category category : categories) {
            MerchantDetailVO.CategoryWithDishes cwd = new MerchantDetailVO.CategoryWithDishes();
            cwd.setCategory(category);

            List<Dish> dishes = dishService.list(new LambdaQueryWrapper<Dish>()
                    .eq(Dish::getCategoryId, category.getId())
                    .eq(Dish::getStatus, Constants.STATUS_ENABLED)
                    .orderByDesc(Dish::getSales));
            cwd.setDishes(dishes);

            categoryList.add(cwd);
        }

        vo.setCategories(categoryList);
        return vo;
    }
}
