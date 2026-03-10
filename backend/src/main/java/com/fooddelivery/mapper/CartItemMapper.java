package com.fooddelivery.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fooddelivery.entity.CartItem;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CartItemMapper extends BaseMapper<CartItem> {
}
