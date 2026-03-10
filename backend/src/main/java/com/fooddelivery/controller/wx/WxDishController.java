package com.fooddelivery.controller.wx;

import com.fooddelivery.common.Result;
import com.fooddelivery.entity.Dish;
import com.fooddelivery.service.DishService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/wx/dish")
@RequiredArgsConstructor
public class WxDishController {

    private final DishService dishService;

    @GetMapping("/list")
    public Result<List<Dish>> list(@RequestParam Long merchantId) {
        return Result.success(dishService.listByMerchantId(merchantId));
    }
}
