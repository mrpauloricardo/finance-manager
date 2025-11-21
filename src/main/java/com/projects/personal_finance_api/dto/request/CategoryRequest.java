package com.projects.personal_finance_api.dto.request;

import com.projects.personal_finance_api.entity.CategoryType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class CategoryRequest {

    @NotBlank(message = "Category name is required")
    @Size(max = 100, message = "Category name must be less than 100 characters")
    private String name;

    @NotNull(message = "Category type is required")
    private CategoryType type;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public CategoryType getType() {
        return type;
    }

    public void setType(CategoryType type) {
        this.type = type;
    }

    public CategoryRequest() {}

    public CategoryRequest(String name, CategoryType type) {
        this.name = name;
        this.type = type;
    }
}
