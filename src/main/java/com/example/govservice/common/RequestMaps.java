package com.example.govservice.common;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public final class RequestMaps {
    private RequestMaps() {
    }

    public static String str(Map<String, Object> body, String key) {
        return str(body, key, "");
    }

    public static String str(Map<String, Object> body, String key, String fallback) {
        Object value = body == null ? null : body.get(key);
        if (value == null) return fallback;
        String text = String.valueOf(value).trim();
        return text.isEmpty() ? fallback : text;
    }

    public static int integer(Map<String, Object> body, String key, int fallback) {
        Object value = body == null ? null : body.get(key);
        if (value == null) return fallback;
        if (value instanceof Number number) return number.intValue();
        try {
            return Integer.parseInt(String.valueOf(value));
        } catch (NumberFormatException ignored) {
            return fallback;
        }
    }

    public static boolean bool(Map<String, Object> body, String key, boolean fallback) {
        Object value = body == null ? null : body.get(key);
        if (value == null) return fallback;
        if (value instanceof Boolean bool) return bool;
        return Boolean.parseBoolean(String.valueOf(value));
    }

    @SuppressWarnings("unchecked")
    public static List<Map<String, Object>> listOfMap(Map<String, Object> body, String key) {
        Object value = body == null ? null : body.get(key);
        if (value instanceof List<?> list) {
            return (List<Map<String, Object>>) list;
        }
        return Collections.emptyList();
    }
}
