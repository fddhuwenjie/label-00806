package com.fooddelivery.controller.admin;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.fooddelivery.common.PageResult;
import com.fooddelivery.common.Result;
import com.fooddelivery.dto.DishDTO;
import com.fooddelivery.dto.PageQueryDTO;
import com.fooddelivery.dto.StatusDTO;
import com.fooddelivery.entity.Dish;
import com.fooddelivery.service.DishService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/dish")
@RequiredArgsConstructor
public class DishController {

    private final DishService dishService;

    @GetMapping("/page")
    public Result<PageResult<Dish>> page(PageQueryDTO dto) {
        IPage<Dish> page = dishService.pageQuery(dto);
        return Result.success(PageResult.of(page.getTotal(), page.getRecords()));
    }

    @GetMapping("/{id}")
    public Result<Dish> getById(@PathVariable Long id) {
        return Result.success(dishService.getById(id));
    }

    @PostMapping
    public Result<Void> save(@Valid @RequestBody DishDTO dto) {
        dishService.saveDish(dto);
        return Result.success();
    }

    @PutMapping
    public Result<Void> update(@Valid @RequestBody DishDTO dto) {
        dishService.updateDish(dto);
        return Result.success();
    }

    @PutMapping("/status")
    public Result<Void> updateStatus(@Valid @RequestBody StatusDTO dto) {
        Dish dish = new Dish();
        dish.setId(dto.getId());
        dish.setStatus(dto.getStatus());
        dishService.updateById(dish);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        dishService.removeById(id);
        return Result.success();
    }
}
