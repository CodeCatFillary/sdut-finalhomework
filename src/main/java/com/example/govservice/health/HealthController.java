package com.example.govservice.health;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/health")
public class HealthController {
    private final JdbcTemplate jdbcTemplate;
    private final StringRedisTemplate redisTemplate;

    public HealthController(JdbcTemplate jdbcTemplate, StringRedisTemplate redisTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.redisTemplate = redisTemplate;
    }

    @GetMapping
    public Map<String, Object> health() {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("status", "UP");
        try {
            result.put("mysql", jdbcTemplate.queryForObject("select 1", Integer.class));
        } catch (Exception exception) {
            result.put("mysql", exception.getMessage());
        }
        try {
            result.put("redis", redisTemplate.getConnectionFactory().getConnection().ping());
        } catch (Exception exception) {
            result.put("redis", exception.getMessage());
        }
        return result;
    }
}
