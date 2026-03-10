package com.fooddelivery.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.fooddelivery.entity.OperationLog;

public interface OperationLogService extends IService<OperationLog> {
    void saveLog(Long adminId, String adminName, String module, String operation, String method, String params, String ip);
}
