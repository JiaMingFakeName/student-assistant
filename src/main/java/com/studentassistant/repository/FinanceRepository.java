package com.studentassistant.repository;

import com.studentassistant.entity.Finance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface FinanceRepository extends JpaRepository<Finance, Long> {

    List<Finance> findByType(String type);

    List<Finance> findByCategory(String category);

    List<Finance> findByTransactionDateBetween(LocalDateTime start, LocalDateTime end);

    @Query("SELECT SUM(f.amount) FROM Finance f WHERE f.type = :type AND f.transactionDate >= :start AND f.transactionDate <= :end")
    BigDecimal getTotalAmountByTypeAndDateRange(@Param("type") String type,
                                                @Param("start") LocalDateTime start,
                                                @Param("end") LocalDateTime end);

    @Query("SELECT f.category, SUM(f.amount) FROM Finance f WHERE f.type = 'support' AND f.transactionDate >= :start AND f.transactionDate <= :end GROUP BY f.category")
    List<Object[]> getExpenseGroupByCategory(@Param("start") LocalDateTime start,
                                             @Param("end") LocalDateTime end);
}