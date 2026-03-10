package com.fooddelivery.controller.admin;

import com.fooddelivery.common.Result;
import com.fooddelivery.dto.CategoryDTO;
import com.fooddelivery.entity.Category;
import com.fooddelivery.service.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/category")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping("/list")
    public Result<List<Category>> list(@RequestParam Long merchantId) {
        return Result.success(categoryService.listByMerchantId(merchantId));
    }

    @PostMapping
    public Result<Void> save(@Valid @RequestBody CategoryDTO dto) {
        categoryService.saveCategory(dto);
        return Result.success();
    }

    @PutMapping
    public Result<Void> update(@Valid @RequestBody CategoryDTO dto) {
        categoryService.updateCategory(dto);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        categoryService.removeById(id);
        return Result.success();
    }
}
