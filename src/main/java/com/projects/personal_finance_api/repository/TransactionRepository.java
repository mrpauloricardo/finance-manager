package com.projects.personal_finance_api.repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.projects.personal_finance_api.entity.CategoryType;
import com.projects.personal_finance_api.entity.Transaction;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    List<Transaction> findByUserId(Long userId);

    List<Transaction> findByUserIdAndDateBetween(Long userId, LocalDate start, LocalDate end);

    boolean existsByUserIdAndDescriptionAndDateAndAmountAndType(
            Long userId,
            String description,
            LocalDate date,
            BigDecimal amount,
            CategoryType type);

    @Query("SELECT SUM(t.amount) FROM Transaction t WHERE t.user.id = :userId AND t.type = :type")
    BigDecimal sumAmountByUserIdAndType(@Param("userId") Long userId,
            @Param("type") CategoryType type);

    @Query("SELECT t FROM Transaction t WHERE t.user.id = :userId " +
            "AND (:type IS NULL OR t.type = :type) " +
            "AND (:categoryId IS NULL OR t.category.id = :categoryId) " +
            "AND t.date BETWEEN :startDate AND :endDate")
    Page<Transaction> findByFilters(
            @Param("userId") Long userId,
            @Param("type") CategoryType type,
            @Param("categoryId") Long categoryId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            Pageable pageable);

    Optional<Transaction> findByIdAndUserId(Long id, Long userId);

    boolean existsByCategoryId(Long categoryId);

    @Query("SELECT t.category.id, SUM(t.amount) FROM Transaction t " +
            "WHERE t.user.id = :userId AND t.type = :type " +
            "GROUP BY t.category.id")
    List<Object[]> sumAmountByUserIdAndTypeGroupByCategory(
            @Param("userId") Long userId,
            @Param("type") CategoryType type);

}
