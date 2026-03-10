package com.fooddelivery.controller.wx;

import com.fooddelivery.common.Result;
import com.fooddelivery.entity.Merchant;
import com.fooddelivery.service.MerchantService;
import com.fooddelivery.vo.MerchantDetailVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/wx/merchant")
@RequiredArgsConstructor
public class WxMerchantController {

    private final MerchantService merchantService;

    @GetMapping("/list")
    public Result<List<Merchant>> list(@RequestParam(required = false) String keyword) {
        return Result.success(merchantService.listForWx(keyword));
    }

    @GetMapping("/{id}")
    public Result<MerchantDetailVO> getById(@PathVariable Long id) {
        return Result.success(merchantService.getDetailForWx(id));
    }
}
