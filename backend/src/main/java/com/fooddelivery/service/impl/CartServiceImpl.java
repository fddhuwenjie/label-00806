package com.fooddelivery.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fooddelivery.dto.CartDTO;
import com.fooddelivery.entity.CartItem;
import com.fooddelivery.entity.Dish;
import com.fooddelivery.entity.Merchant;
import com.fooddelivery.mapper.CartItemMapper;
import com.fooddelivery.service.CartService;
import com.fooddelivery.service.DishService;
import com.fooddelivery.service.MerchantService;
import com.fooddelivery.vo.CartVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CartServiceImpl extends ServiceImpl<CartItemMapper, CartItem> implements CartService {

    private final DishService dishService;
    private final MerchantService merchantService;

    @Override
    public List<CartVO> listByUserId(Long userId) {
        List<CartItem> items = list(new LambdaQueryWrapper<CartItem>()
                .eq(CartItem::getUserId, userId)
                .orderByDesc(CartItem::getCreateTime));
        return convertToVO(items);
    }

    @Override
    public List<CartVO> listByUserIdAndMerchantId(Long userId, Long merchantId) {
        List<CartItem> items = list(new LambdaQueryWrapper<CartItem>()
                .eq(CartItem::getUserId, userId)
                .eq(CartItem::getMerchantId, merchantId)
                .orderByDesc(CartItem::getCreateTime));
        return convertToVO(items);
    }

    private List<CartVO> convertToVO(List<CartItem> items) {
        List<CartVO> voList = new ArrayList<>();
        for (CartItem item : items) {
            CartVO vo = new CartVO();
            vo.setId(item.getId());
            vo.setMerchantId(item.getMerchantId());
            vo.setDishId(item.getDishId());
            vo.setQuantity(item.getQuantity());

            Merchant merchant = merchantService.getById(item.getMerchantId());
            if (merchant != null) {
                vo.setMerchantName(merchant.getName());
            }

            Dish dish = dishService.getById(item.getDishId());
            if (dish != null) {
                vo.setDishName(dish.getName());
                vo.setDishImage(dish.getImage());
                vo.setDishPrice(dish.getPrice());
            }

            voList.add(vo);
        }
        return voList;
    }

    @Override
    public void addCart(Long userId, CartDTO dto) {
        // 验证必要参数
        if (dto.getDishId() == null) {
            throw new com.fooddelivery.common.BusinessException("菜品ID不能为空");
        }
        if (dto.getMerchantId() == null) {
            throw new com.fooddelivery.common.BusinessException("商家ID不能为空");
        }
        
        // 检查是否已存在（同一用户、同一商家、同一菜品）
        CartItem existing = getOne(new LambdaQueryWrapper<CartItem>()
                .eq(CartItem::getUserId, userId)
                .eq(CartItem::getMerchantId, dto.getMerchantId())
                .eq(CartItem::getDishId, dto.getDishId()));

        if (existing != null) {
            int oldQty = existing.getQuantity();
            int newQty = oldQty + dto.getQuantity();
            existing.setQuantity(newQty);
            boolean updated = updateById(existing);
            log.info("更新购物车: id={}, oldQty={}, newQty={}, updated={}", existing.getId(), oldQty, newQty, updated);
        } else {
            CartItem item = new CartItem();
            item.setUserId(userId);
            item.setMerchantId(dto.getMerchantId());
            item.setDishId(dto.getDishId());
            item.setQuantity(dto.getQuantity());
            boolean saved = save(item);
            log.info("新增购物车: dishId={}, saved={}", dto.getDishId(), saved);
        }
        log.info("添加购物车完成: userId={}, merchantId={}, dishId={}", userId, dto.getMerchantId(), dto.getDishId());
    }

    @Override
    public void updateCart(Long userId, CartDTO dto) {
        CartItem item = getById(dto.getId());
        if (item != null && item.getUserId().equals(userId)) {
            if (dto.getQuantity() <= 0) {
                removeById(dto.getId());
            } else {
                item.setQuantity(dto.getQuantity());
                updateById(item);
            }
        }
        log.info("更新购物车: id={}", dto.getId());
    }

    @Override
    public void deleteCart(Long userId, Long id) {
        remove(new LambdaQueryWrapper<CartItem>()
                .eq(CartItem::getId, id)
                .eq(CartItem::getUserId, userId));
        log.info("删除购物车: id={}", id);
    }

    @Override
    public void clearCart(Long userId, Long merchantId) {
        remove(new LambdaQueryWrapper<CartItem>()
                .eq(CartItem::getUserId, userId)
                .eq(CartItem::getMerchantId, merchantId));
        log.info("清空购物车: userId={}, merchantId={}", userId, merchantId);
    }
}
