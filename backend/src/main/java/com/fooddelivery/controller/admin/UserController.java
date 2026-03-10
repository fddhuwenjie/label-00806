package com.fooddelivery.controller.admin;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.fooddelivery.common.PageResult;
import com.fooddelivery.common.Result;
import com.fooddelivery.dto.PageQueryDTO;
import com.fooddelivery.dto.StatusDTO;
import com.fooddelivery.entity.User;
import com.fooddelivery.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/page")
    public Result<PageResult<User>> page(PageQueryDTO dto) {
        IPage<User> page = userService.pageQuery(dto);
        return Result.success(PageResult.of(page.getTotal(), page.getRecords()));
    }

    @PutMapping("/status")
    public Result<Void> updateStatus(@Valid @RequestBody StatusDTO dto) {
        User user = new User();
        user.setId(dto.getId());
        user.setStatus(dto.getStatus());
        userService.updateById(user);
        return Result.success();
    }
}
