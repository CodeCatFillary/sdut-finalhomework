package com.example.govservice.common;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

public final class IdGenerator {
    private static final DateTimeFormatter DAY = DateTimeFormatter.ofPattern("yyyyMMdd");

    private IdGenerator() {
    }

    public static String textId(String prefix) {
        String random = UUID.randomUUID().toString().replace("-", "").substring(0, 12);
        return prefix + "-" + random;
    }

    public static String applicationNo() {
        int number = ThreadLocalRandom.current().nextInt(1000, 9999);
        return "ZW" + DAY.format(LocalDateTime.now()) + number;
    }
}
