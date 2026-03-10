package com.fooddelivery.controller.wx;

import com.fooddelivery.common.BaseContext;
import com.fooddelivery.common.Result;
import com.fooddelivery.dto.WxLoginDTO;
import com.fooddelivery.entity.User;
import com.fooddelivery.service.UserService;
import com.fooddelivery.vo.WxLoginVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/wx/auth")
@RequiredArgsConstructor
public class WxAuthController {

    private final UserService userService;

    @PostMapping("/login")
    public Result<WxLoginVO> login(@Valid @RequestBody WxLoginDTO dto) {
        return Result.success(userService.wxLogin(dto));
    }

    @GetMapping("/info")
    public Result<User> info() {
        Long userId = BaseContext.getUserId();
        return Result.success(userService.getInfo(userId));
    }

    @PutMapping("/info")
    public Result<Void> updateInfo(@RequestBody User user) {
        Long userId = BaseContext.getUserId();
        user.setId(userId);
        user.setOpenid(null); // 不允许修改openid
        userService.updateById(user);
        return Result.success();
    }
}
