package com.projects.personal_finance_api.dto.response;

import com.projects.personal_finance_api.entity.User;

public record UserResponse(
        Long id,
        String username,
        String email,
        String createdAt) {
    public static UserResponse fromEntity(User user) {
        return new UserResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getCreatedAt().toString());
    }
}
