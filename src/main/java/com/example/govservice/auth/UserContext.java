package com.example.govservice.auth;

import java.util.Map;

public final class UserContext {
    private static final ThreadLocal<Map<String, Object>> USER = new ThreadLocal<>();

    private UserContext() {
    }

    public static void set(Map<String, Object> user) {
        USER.set(user);
    }

    public static Map<String, Object> get() {
        return USER.get();
    }

    public static void clear() {
        USER.remove();
    }
}
