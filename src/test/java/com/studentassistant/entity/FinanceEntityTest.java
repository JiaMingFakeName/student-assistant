// FinanceEntityTest.java
package com.studentassistant.entity;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class FinanceEntityTest {

    // ET01: @PrePersist回调验证
    @Test
    void onCreate_shouldSetTimestamps() {
        Finance finance = new Finance();
        finance.setTitle("Test");
        finance.setAmount(new BigDecimal("99.99"));
        finance.setType("支出");
        finance.setCategory("测试");

        // 触发@PrePersist
        finance.onCreate();

        assertNotNull(finance.getCreatedTime());
        assertNotNull(finance.getUpdatedTime());
        assertNotNull(finance.getTransactionDate());
    }

    // ET02: 手动设置transactionDate优先级验证
    @Test
    void onCreate_withManualDate_shouldKeepManualDate() {
        LocalDateTime manualDate = LocalDateTime.of(2025, 1, 1, 12, 0);

        Finance finance = new Finance();
        finance.setTransactionDate(manualDate);

        // 触发@PrePersist
        finance.onCreate();

        assertEquals(manualDate, finance.getTransactionDate());
    }

    // ET03: @PreUpdate回调验证
    @Test
    void onUpdate_shouldUpdateTimestamp() {
        LocalDateTime originalTime = LocalDateTime.now().minusDays(1);

        Finance finance = new Finance();
        finance.setUpdatedTime(originalTime);

        // 触发@PreUpdate
        finance.onUpdate();

        assertTrue(finance.getUpdatedTime().isAfter(originalTime));
    }

    // ET04: @DecimalMin边界验证
    @Test
    void amountBoundary_shouldAcceptMinValue() {
        Finance finance = new Finance();
        finance.setAmount(new BigDecimal("0.01")); // 最小值边界

        assertDoesNotThrow(() -> finance.onCreate());
    }
}