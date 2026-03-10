package com.fooddelivery.controller.wx;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.fooddelivery.common.BaseContext;
import com.fooddelivery.common.PageResult;
import com.fooddelivery.common.Result;
import com.fooddelivery.dto.OrderSubmitDTO;
import com.fooddelivery.service.OrderService;
import com.fooddelivery.vo.OrderVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/wx/order")
@RequiredArgsConstructor
public class WxOrderController {

    private final OrderService orderService;

    @PostMapping("/submit")
    public Result<OrderVO> submit(@Valid @RequestBody OrderSubmitDTO dto) {
        Long userId = BaseContext.getUserId();
        return Result.success(orderService.submitOrder(userId, dto));
    }

    @GetMapping("/list")
    public Result<PageResult<OrderVO>> list(
            @RequestParam(required = false) Integer status,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        Long userId = BaseContext.getUserId();
        IPage<OrderVO> pageResult = orderService.pageQueryForUser(userId, status, page, pageSize);
        return Result.success(PageResult.of(pageResult.getTotal(), pageResult.getRecords()));
    }

    @GetMapping("/{id}")
    public Result<OrderVO> getById(@PathVariable Long id) {
        return Result.success(orderService.getDetail(id));
    }

    @PutMapping("/cancel")
    public Result<Void> cancel(@RequestBody java.util.Map<String, Object> body) {
        Long userId = BaseContext.getUserId();
        Long id = Long.valueOf(body.get("id").toString());
        orderService.cancelOrder(userId, id);
        return Result.success();
    }

    @PutMapping("/confirm")
    public Result<Void> confirm(@RequestBody java.util.Map<String, Object> body) {
        Long userId = BaseContext.getUserId();
        Long id = Long.valueOf(body.get("id").toString());
        orderService.confirmOrder(userId, id);
        return Result.success();
    }

    @PutMapping("/pay")
    public Result<Void> pay(@RequestBody java.util.Map<String, Object> body) {
        Long userId = BaseContext.getUserId();
        Long id = Long.valueOf(body.get("id").toString());
        orderService.payOrder(userId, id);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        Long userId = BaseContext.getUserId();
        orderService.deleteOrder(userId, id);
        return Result.success();
    }
}
