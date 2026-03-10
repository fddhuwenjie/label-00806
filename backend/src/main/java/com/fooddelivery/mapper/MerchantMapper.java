package com.fooddelivery.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fooddelivery.entity.Merchant;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface MerchantMapper extends BaseMapper<Merchant> {
}
