package com.projects.personal_finance_api.dto.response;

import com.projects.personal_finance_api.entity.Category;
import com.projects.personal_finance_api.entity.CategoryType;

public record CategoryResponse(
        Long id,
        String name,
        CategoryType type,
        String createdAt) {
    public static CategoryResponse fromEntity(Category category) {
        return new CategoryResponse(
                category.getId(),
                category.getName(),
                category.getType(),
                category.getCreatedAt().toString());
    }
}
