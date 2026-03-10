package com.fooddelivery.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fooddelivery.entity.Dish;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface DishMapper extends BaseMapper<Dish> {
}
