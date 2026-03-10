package com.fooddelivery.controller.admin;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.fooddelivery.common.PageResult;
import com.fooddelivery.common.Result;
import com.fooddelivery.dto.MerchantDTO;
import com.fooddelivery.dto.PageQueryDTO;
import com.fooddelivery.dto.StatusDTO;
import com.fooddelivery.entity.Merchant;
import com.fooddelivery.service.MerchantService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/merchant")
@RequiredArgsConstructor
public class MerchantController {

    private final MerchantService merchantService;

    @GetMapping("/page")
    public Result<PageResult<Merchant>> page(PageQueryDTO dto) {
        IPage<Merchant> page = merchantService.pageQuery(dto);
        return Result.success(PageResult.of(page.getTotal(), page.getRecords()));
    }

    @GetMapping("/{id}")
    public Result<Merchant> getById(@PathVariable Long id) {
        return Result.success(merchantService.getById(id));
    }

    @PostMapping
    public Result<Void> save(@Valid @RequestBody MerchantDTO dto) {
        merchantService.saveMerchant(dto);
        return Result.success();
    }

    @PutMapping
    public Result<Void> update(@Valid @RequestBody MerchantDTO dto) {
        merchantService.updateMerchant(dto);
        return Result.success();
    }

    @PutMapping("/status")
    public Result<Void> updateStatus(@Valid @RequestBody StatusDTO dto) {
        Merchant merchant = new Merchant();
        merchant.setId(dto.getId());
        merchant.setStatus(dto.getStatus());
        merchantService.updateById(merchant);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        merchantService.removeById(id);
        return Result.success();
    }
}
