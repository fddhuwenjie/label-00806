package com.fooddelivery.aspect;

import cn.hutool.json.JSONUtil;
import com.fooddelivery.common.BaseContext;
import com.fooddelivery.entity.Admin;
import com.fooddelivery.service.AdminService;
import com.fooddelivery.service.OperationLogService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class OperationLogAspect {

    private final OperationLogService operationLogService;
    private final AdminService adminService;

    @Pointcut("execution(* com.fooddelivery.controller.admin.*.*(..)) && " +
            "(@annotation(org.springframework.web.bind.annotation.PostMapping) || " +
            "@annotation(org.springframework.web.bind.annotation.PutMapping) || " +
            "@annotation(org.springframework.web.bind.annotation.DeleteMapping))")
    public void logPointcut() {}

    @Around("logPointcut()")
    public Object around(ProceedingJoinPoint point) throws Throwable {
        Object result = point.proceed();

        try {
            Long adminId = BaseContext.getAdminId();
            if (adminId != null) {
                Admin admin = adminService.getById(adminId);
                String adminName = admin != null ? admin.getUsername() : "unknown";

                String className = point.getTarget().getClass().getSimpleName();
                String methodName = point.getSignature().getName();
                String module = className.replace("Controller", "");
                String operation = getOperation(methodName);
                String method = className + "." + methodName;
                String params = JSONUtil.toJsonStr(point.getArgs());

                HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
                String ip = getIpAddress(request);

                operationLogService.saveLog(adminId, adminName, module, operation, method, params, ip);
            }
        } catch (Exception e) {
            log.error("记录操作日志失败", e);
        }

        return result;
    }

    private String getOperation(String methodName) {
        if (methodName.startsWith("save") || methodName.startsWith("add")) {
            return "新增";
        } else if (methodName.startsWith("update") || methodName.startsWith("edit")) {
            return "修改";
        } else if (methodName.startsWith("delete") || methodName.startsWith("remove")) {
            return "删除";
        }
        return "操作";
    }

    private String getIpAddress(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }
}
