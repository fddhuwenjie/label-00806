package com.fooddelivery.vo;

import com.fooddelivery.entity.Category;
import com.fooddelivery.entity.Dish;
import com.fooddelivery.entity.Merchant;
import lombok.Data;
import java.util.List;

@Data
public class MerchantDetailVO {
    private Merchant merchant;
    private List<CategoryWithDishes> categories;

    @Data
    public static class CategoryWithDishes {
        private Category category;
        private List<Dish> dishes;
    }
}
