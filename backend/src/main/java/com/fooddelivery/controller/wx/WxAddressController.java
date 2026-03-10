package com.fooddelivery.controller.wx;

import com.fooddelivery.common.BaseContext;
import com.fooddelivery.common.Result;
import com.fooddelivery.dto.AddressDTO;
import com.fooddelivery.entity.Address;
import com.fooddelivery.service.AddressService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/wx/address")
@RequiredArgsConstructor
public class WxAddressController {

    private final AddressService addressService;

    @GetMapping("/list")
    public Result<List<Address>> list() {
        Long userId = BaseContext.getUserId();
        return Result.success(addressService.listByUserId(userId));
    }

    @GetMapping("/default")
    public Result<Address> getDefault() {
        Long userId = BaseContext.getUserId();
        return Result.success(addressService.getDefault(userId));
    }

    @PostMapping
    public Result<Void> save(@Valid @RequestBody AddressDTO dto) {
        Long userId = BaseContext.getUserId();
        addressService.saveAddress(userId, dto);
        return Result.success();
    }

    @PutMapping
    public Result<Void> update(@Valid @RequestBody AddressDTO dto) {
        Long userId = BaseContext.getUserId();
        addressService.updateAddress(userId, dto);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        Long userId = BaseContext.getUserId();
        addressService.deleteAddress(userId, id);
        return Result.success();
    }
}
