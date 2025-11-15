package com.projects.personal_finance_api.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import com.projects.personal_finance_api.entity.Category;
import com.projects.personal_finance_api.entity.CategoryType;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    List<Category> findByUserId(Long userId);

    List<Category> findByUserIdAndType(Long userId, CategoryType type);

    boolean existsByUserIdAndNameIgnoreCase(Long userId, String name);

    Optional<Category> findByIdAndUserId(Long id, Long userId);
}
