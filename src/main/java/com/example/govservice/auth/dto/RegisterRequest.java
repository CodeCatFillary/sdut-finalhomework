package com.example.govservice.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
        @NotBlank(message = "不能为空") String username,
        @NotBlank(message = "不能为空") @Size(min = 6, message = "不能少于6位") String password,
        @NotBlank(message = "不能为空") String name,
        String role,
        String department,
        String phone
) {
}
