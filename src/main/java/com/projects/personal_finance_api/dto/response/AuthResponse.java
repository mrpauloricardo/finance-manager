package com.projects.personal_finance_api.dto.response;

public record AuthResponse(
        String token,
        String tokenType,
        String username,
        String email) {
}
