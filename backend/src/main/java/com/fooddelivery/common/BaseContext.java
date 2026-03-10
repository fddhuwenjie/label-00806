package com.fooddelivery.common;

public class BaseContext {
    private static final ThreadLocal<Long> adminId = new ThreadLocal<>();
    private static final ThreadLocal<Long> userId = new ThreadLocal<>();

    public static void setAdminId(Long id) {
        adminId.set(id);
    }

    public static Long getAdminId() {
        return adminId.get();
    }

    public static void setUserId(Long id) {
        userId.set(id);
    }

    public static Long getUserId() {
        return userId.get();
    }

    public static void clear() {
        adminId.remove();
        userId.remove();
    }
}
