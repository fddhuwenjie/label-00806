package com.fooddelivery.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fooddelivery.entity.OperationLog;
import com.fooddelivery.mapper.OperationLogMapper;
import com.fooddelivery.service.OperationLogService;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class OperationLogServiceImpl extends ServiceImpl<OperationLogMapper, OperationLog> implements OperationLogService {

    @Override
    @Async
    public void saveLog(Long adminId, String adminName, String module, String operation, String method, String params, String ip) {
        OperationLog log = new OperationLog();
        log.setAdminId(adminId);
        log.setAdminName(adminName);
        log.setModule(module);
        log.setOperation(operation);
        log.setMethod(method);
        log.setParams(params);
        log.setIp(ip);
        save(log);
    }
}
