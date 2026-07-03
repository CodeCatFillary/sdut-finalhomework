package com.example.govservice.auth.dto;

import java.util.Map;

public record AuthResponse(String token, long expiresIn, Map<String, Object> user) {
}
