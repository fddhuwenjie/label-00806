package com.fooddelivery.controller.admin;

import com.fooddelivery.common.Result;
import com.fooddelivery.service.DashboardService;
import com.fooddelivery.vo.DashboardVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/statistics")
    public Result<DashboardVO> statistics() {
        return Result.success(dashboardService.getStatistics());
    }
}
