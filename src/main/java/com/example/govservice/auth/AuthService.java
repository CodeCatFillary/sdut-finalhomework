package com.example.govservice.auth;

import com.example.govservice.auth.dto.AuthResponse;
import com.example.govservice.auth.dto.LoginRequest;
import com.example.govservice.auth.dto.RegisterRequest;
import com.example.govservice.common.BusinessException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
public class AuthService {
    private static final String TOKEN_PREFIX = "gov:auth:token:";

    private final JdbcTemplate jdbcTemplate;
    private final PasswordEncoder passwordEncoder;
    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;
    private final long tokenTtlSeconds;

    public AuthService(JdbcTemplate jdbcTemplate,
                       PasswordEncoder passwordEncoder,
                       StringRedisTemplate redisTemplate,
                       ObjectMapper objectMapper,
                       @Value("${app.auth.token-ttl-seconds:7200}") long tokenTtlSeconds) {
        this.jdbcTemplate = jdbcTemplate;
        this.passwordEncoder = passwordEncoder;
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
        this.tokenTtlSeconds = tokenTtlSeconds;
    }

    public AuthResponse login(LoginRequest request) {
        Map<String, Object> account;
        try {
            account = jdbcTemplate.queryForMap("select * from sys_user where username = ? and status = '正常'", request.username().trim());
        } catch (EmptyResultDataAccessException exception) {
            throw new BusinessException("账号或密码错误");
        }

        String passwordHash = String.valueOf(account.get("password_hash"));
        boolean passwordMatched = passwordHash.startsWith("{plain}")
                ? request.password().equals(passwordHash.substring("{plain}".length()))
                : passwordEncoder.matches(request.password(), passwordHash);
        if (!passwordMatched) {
            throw new BusinessException("账号或密码错误");
        }

        return createToken(account);
    }

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        int exists = jdbcTemplate.queryForObject("select count(1) from sys_user where username = ?", Integer.class, request.username().trim());
        if (exists > 0) {
            throw new BusinessException(409, "该账号已存在");
        }

        String role = blankToDefault(request.role(), "普通用户");
        String department = blankToDefault(request.department(), "个人办事端");
        String phone = blankToDefault(request.phone(), "");
        String passwordHash = passwordEncoder.encode(request.password());

        jdbcTemplate.update("""
                insert into sys_user(username, password_hash, name, role, department, phone, status, created_at)
                values (?, ?, ?, ?, ?, ?, '正常', ?)
                """, request.username().trim(), passwordHash, request.name().trim(), role, department, phone, LocalDateTime.now());

        Map<String, Object> account = jdbcTemplate.queryForMap("select * from sys_user where username = ?", request.username().trim());
        return createToken(account);
    }

    public void logout(String token) {
        if (token != null && !token.isBlank()) {
            redisTemplate.delete(TOKEN_PREFIX + token);
        }
    }

    public Map<String, Object> getUserByToken(String token) {
        try {
            String json = redisTemplate.opsForValue().get(TOKEN_PREFIX + token);
            if (json == null || json.isBlank()) {
                return null;
            }
            redisTemplate.expire(TOKEN_PREFIX + token, tokenTtlSeconds, TimeUnit.SECONDS);
            return objectMapper.readValue(json, new TypeReference<Map<String, Object>>() {});
        } catch (Exception exception) {
            throw new BusinessException(401, "登录状态校验失败");
        }
    }

    private AuthResponse createToken(Map<String, Object> account) {
        String token = UUID.randomUUID().toString().replace("-", "");
        Map<String, Object> user = sanitizeUser(account);

        try {
            redisTemplate.opsForValue().set(TOKEN_PREFIX + token, objectMapper.writeValueAsString(user), tokenTtlSeconds, TimeUnit.SECONDS);
        } catch (Exception exception) {
            throw new BusinessException(500, "Redis 登录态写入失败 请检查 Redis 是否启动");
        }

        return new AuthResponse(token, tokenTtlSeconds, user);
    }

    private Map<String, Object> sanitizeUser(Map<String, Object> account) {
        Map<String, Object> user = new LinkedHashMap<>();
        user.put("id", account.get("id"));
        user.put("username", account.get("username"));
        user.put("name", account.get("name"));
        user.put("role", account.get("role"));
        user.put("department", account.get("department"));
        user.put("phone", account.get("phone"));
        user.put("loginAt", LocalDateTime.now());
        return user;
    }

    private String blankToDefault(String value, String fallback) {
        return value == null || value.isBlank() ? fallback : value.trim();
    }
}
