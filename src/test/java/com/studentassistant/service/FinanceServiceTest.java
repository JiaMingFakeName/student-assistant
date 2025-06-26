// FinanceServiceTest.java
package com.studentassistant.service;

import com.studentassistant.dto.FinanceDTO;
import com.studentassistant.entity.Finance;
import com.studentassistant.repository.FinanceRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.BeanUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class FinanceServiceTest {

    @Mock
    private FinanceRepository financeRepository;

    @InjectMocks
    private FinanceService financeService;

    // ST01: 正常创建路径覆盖
    @Test
    void createFinance_shouldPersistWithTimestamps() {
        FinanceDTO dto = new FinanceDTO();
        dto.setTitle("Test");
        dto.setAmount(new BigDecimal("100.50"));
        dto.setType("支出");
        dto.setCategory("测试");

        Finance finance = new Finance();
        BeanUtils.copyProperties(dto, finance);
        finance.setId(1L);
        // 手动设置时间戳
        finance.onCreate(); // 新增此行

        when(financeRepository.save(any(Finance.class))).thenReturn(finance);

        Finance result = financeService.createFinance(dto);

        assertNotNull(result);
        assertNotNull(result.getCreatedTime());
        assertNotNull(result.getUpdatedTime());
        assertEquals(1L, result.getId());
    }

    // ST02: ID不存在分支覆盖
    @Test
    void updateFinance_notFound_shouldThrow() {
        FinanceDTO dto = new FinanceDTO();
        dto.setTitle("Update");
        dto.setAmount(new BigDecimal("200"));
        dto.setType("收入");
        dto.setCategory("测试更新");

        when(financeRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> {
            financeService.updateFinance(999L, dto);
        });
    }

    // ST03: 数据流验证（updatedTime）
    @Test
    void updateFinance_shouldUpdateTimestamp() {
        LocalDateTime originalTime = LocalDateTime.now().minusDays(1);
        Finance existing = new Finance(1L, "Original", new BigDecimal("50"), "支出", "测试",
                null, LocalDateTime.now(), originalTime, originalTime);

        // 手动初始化时间戳
        existing.onCreate(); // 新增此行

        FinanceDTO dto = new FinanceDTO();
        dto.setTitle("Updated");
        dto.setAmount(new BigDecimal("100"));
        dto.setType("收入");
        dto.setCategory("更新");

        when(financeRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(financeRepository.save(any(Finance.class))).thenAnswer(invocation -> {
            Finance updated = invocation.getArgument(0);
            return updated;
        });

        Finance result = financeService.updateFinance(1L, dto);

        assertTrue(result.getUpdatedTime().isAfter(originalTime));
        assertEquals("Updated", result.getTitle());
        assertEquals("收入", result.getType());
    }

    // ST04: 空结果集处理
    @Test
    void getTotalAmountByType_noRecords_shouldReturnZero() {
        LocalDateTime start = LocalDateTime.now().minusDays(7);
        LocalDateTime end = LocalDateTime.now();

        when(financeRepository.getTotalAmountByTypeAndDateRange("收入", start, end))
                .thenReturn(null);

        BigDecimal result = financeService.getTotalAmountByType("收入", start, end);
        assertEquals(BigDecimal.ZERO, result);
    }

    // ST05: 收支计算逻辑
    @Test
    void getBalance_shouldCalculateCorrectly() {
        LocalDateTime start = LocalDateTime.of(2025, 1, 1, 0, 0);
        LocalDateTime end = LocalDateTime.of(2025, 1, 31, 23, 59);

        when(financeRepository.getTotalAmountByTypeAndDateRange("收入", start, end))
                .thenReturn(new BigDecimal("1000"));
        when(financeRepository.getTotalAmountByTypeAndDateRange("支出", start, end))
                .thenReturn(new BigDecimal("750"));

        BigDecimal balance = financeService.getBalance(start, end);
        assertEquals(new BigDecimal("250"), balance);
    }

    // ST06: 只有收入分支覆盖
    @Test
    void getBalance_onlyIncome_shouldReturnPositive() {
        LocalDateTime start = LocalDateTime.now().minusDays(30);
        LocalDateTime end = LocalDateTime.now();

        when(financeRepository.getTotalAmountByTypeAndDateRange("收入", start, end))
                .thenReturn(new BigDecimal("3000"));
        when(financeRepository.getTotalAmountByTypeAndDateRange("支出", start, end))
                .thenReturn(null);

        BigDecimal balance = financeService.getBalance(start, end);
        assertEquals(new BigDecimal("3000"), balance);
    }

    // ST07: 只有支出分支覆盖
    @Test
    void getBalance_onlyExpense_shouldReturnNegative() {
        LocalDateTime start = LocalDateTime.now().minusDays(30);
        LocalDateTime end = LocalDateTime.now();

        when(financeRepository.getTotalAmountByTypeAndDateRange("收入", start, end))
                .thenReturn(null);
        when(financeRepository.getTotalAmountByTypeAndDateRange("支出", start, end))
                .thenReturn(new BigDecimal("1200"));

        BigDecimal balance = financeService.getBalance(start, end);
        assertEquals(new BigDecimal("-1200"), balance);
    }
}