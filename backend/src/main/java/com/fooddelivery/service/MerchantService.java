package com.fooddelivery.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.fooddelivery.dto.MerchantDTO;
import com.fooddelivery.dto.PageQueryDTO;
import com.fooddelivery.entity.Merchant;
import com.fooddelivery.vo.MerchantDetailVO;

import java.util.List;

public interface MerchantService extends IService<Merchant> {
    IPage<Merchant> pageQuery(PageQueryDTO dto);
    void saveMerchant(MerchantDTO dto);
    void updateMerchant(MerchantDTO dto);
    List<Merchant> listForWx(String keyword);
    MerchantDetailVO getDetailForWx(Long id);
}
