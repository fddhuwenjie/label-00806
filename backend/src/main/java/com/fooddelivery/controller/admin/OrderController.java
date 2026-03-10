package com.fooddelivery.controller.admin;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.fooddelivery.common.PageResult;
import com.fooddelivery.common.Result;
import com.fooddelivery.dto.PageQueryDTO;
import com.fooddelivery.dto.StatusDTO;
import com.fooddelivery.service.OrderService;
import com.fooddelivery.vo.OrderVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/order")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @GetMapping("/page")
    public Result<PageResult<OrderVO>> page(PageQueryDTO dto) {
        IPage<OrderVO> page = orderService.pageQuery(dto);
        return Result.success(PageResult.of(page.getTotal(), page.getRecords()));
    }

    @GetMapping("/{id}")
    public Result<OrderVO> getById(@PathVariable Long id) {
        return Result.success(orderService.getDetail(id));
    }

    @PutMapping("/status")
    public Result<Void> updateStatus(@Valid @RequestBody StatusDTO dto) {
        orderService.updateStatus(dto.getId(), dto.getStatus());
        return Result.success();
    }
}
