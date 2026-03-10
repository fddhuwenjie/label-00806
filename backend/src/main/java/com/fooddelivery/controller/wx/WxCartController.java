package com.fooddelivery.controller.wx;

import com.fooddelivery.common.BaseContext;
import com.fooddelivery.common.Result;
import com.fooddelivery.dto.CartDTO;
import com.fooddelivery.service.CartService;
import com.fooddelivery.vo.CartVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/wx/cart")
@RequiredArgsConstructor
public class WxCartController {

    private final CartService cartService;

    @GetMapping("/list")
    public Result<List<CartVO>> list(@RequestParam(required = false) Long merchantId) {
        Long userId = BaseContext.getUserId();
        if (merchantId != null) {
            return Result.success(cartService.listByUserIdAndMerchantId(userId, merchantId));
        }
        return Result.success(cartService.listByUserId(userId));
    }

    @PostMapping("/add")
    public Result<Void> add(@Valid @RequestBody CartDTO dto) {
        Long userId = BaseContext.getUserId();
        cartService.addCart(userId, dto);
        return Result.success();
    }

    @PutMapping("/update")
    public Result<Void> update(@Valid @RequestBody CartDTO dto) {
        Long userId = BaseContext.getUserId();
        cartService.updateCart(userId, dto);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        Long userId = BaseContext.getUserId();
        cartService.deleteCart(userId, id);
        return Result.success();
    }

    @DeleteMapping("/clear")
    public Result<Void> clear(@RequestBody java.util.Map<String, Object> body) {
        Long userId = BaseContext.getUserId();
        Long merchantId = Long.valueOf(body.get("merchantId").toString());
        cartService.clearCart(userId, merchantId);
        return Result.success();
    }
}
