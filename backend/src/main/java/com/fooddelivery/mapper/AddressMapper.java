package com.fooddelivery.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fooddelivery.entity.Address;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AddressMapper extends BaseMapper<Address> {
}
