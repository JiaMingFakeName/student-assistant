// FinanceControllerTest.java
package com.studentassistant.controller;

import com.studentassistant.entity.Finance;
import com.studentassistant.dto.FinanceDTO;
import com.studentassistant.service.FinanceService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(FinanceController.class)
public class FinanceControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private FinanceService financeService;

    // CT01: 完整统计路径覆盖
    @Test
    void getFinanceStatistics_shouldReturnStats() throws Exception {
        LocalDateTime start = LocalDateTime.of(2025, 1, 1, 0, 0);
        LocalDateTime end = LocalDateTime.of(2025, 1, 31, 23, 59);

        Finance record1 = new Finance(1L, "Salary", new BigDecimal("5000"), "收入", "工资", "Monthly salary",
                LocalDateTime.now(), null, null);
        Finance record2 = new Finance(2L, "Rent", new BigDecimal("1500"), "支出", "住房", "Apartment rent",
                LocalDateTime.now(), null, null);

        Map<String, Object> stats = new HashMap<>();
        stats.put("totalRecords", 2);
        stats.put("totalIncome", new BigDecimal("5000"));
        stats.put("totalExpense", new BigDecimal("1500"));
        stats.put("balance", new BigDecimal("3500"));
        stats.put("records", Arrays.asList(record1, record2));

        Mockito.when(financeService.getFinancesByDateRange(start, end))
                .thenReturn(Arrays.asList(record1, record2));
        Mockito.when(financeService.getTotalAmountByType("收入", start, end))
                .thenReturn(new BigDecimal("5000"));
        Mockito.when(financeService.getTotalAmountByType("支出", start, end))
                .thenReturn(new BigDecimal("1500"));
        Mockito.when(financeService.getBalance(start, end))
                .thenReturn(new BigDecimal("3500"));

        mockMvc.perform(MockMvcRequestBuilders.get("/finance/statistics")
                        .param("start", start.toString())
                        .param("end", end.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalRecords").value(2))
                .andExpect(jsonPath("$.totalIncome").value(5000))
                .andExpect(jsonPath("$.totalExpense").value(1500))
                .andExpect(jsonPath("$.balance").value(3500));
    }

    // CT02: ID不存在分支覆盖
    @Test
    void getFinanceById_notFound() throws Exception {
        Mockito.when(financeService.getFinanceById(999L)).thenReturn(Optional.empty());

        mockMvc.perform(MockMvcRequestBuilders.get("/finance/999"))
                .andExpect(status().isNotFound());
    }

    // CT03: DTO约束验证
    @Test
    void createFinance_invalidAmount_shouldFail() throws Exception {
        String invalidJson = "{\"title\":\"Test\",\"amount\":0,\"type\":\"支出\",\"category\":\"测试\"}";

        mockMvc.perform(MockMvcRequestBuilders.post("/finance")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().isBadRequest());
    }

    // CT04: 集合返回能力验证
    @Test
    void getFinancesByType_shouldReturnList() throws Exception {
        Finance expense1 = new Finance(1L, "Rent", new BigDecimal("1500"), "支出", "住房", null,
                LocalDateTime.now(), null, null);
        Finance expense2 = new Finance(2L, "Food", new BigDecimal("300"), "支出", "餐饮", null,
                LocalDateTime.now(), null, null);

        Mockito.when(financeService.getFinancesByType("支出"))
                .thenReturn(Arrays.asList(expense1, expense2));

        mockMvc.perform(MockMvcRequestBuilders.get("/finance/type/支出"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].title").value("Rent"))
                .andExpect(jsonPath("$[1].title").value("Food"));
    }

    // CT05: 时间边界值验证
    @Test
    void getFinanceStatistics_sameStartEnd_shouldWork() throws Exception {
        LocalDateTime sameTime = LocalDateTime.of(2025, 1, 1, 0, 0);

        Map<String, Object> stats = new HashMap<>();
        stats.put("totalRecords", 0);
        stats.put("totalIncome", BigDecimal.ZERO);
        stats.put("totalExpense", BigDecimal.ZERO);
        stats.put("balance", BigDecimal.ZERO);
        stats.put("records", Collections.emptyList());

        Mockito.when(financeService.getFinancesByDateRange(sameTime, sameTime))
                .thenReturn(Collections.emptyList());
        Mockito.when(financeService.getTotalAmountByType("收入", sameTime, sameTime))
                .thenReturn(BigDecimal.ZERO);
        Mockito.when(financeService.getTotalAmountByType("支出", sameTime, sameTime))
                .thenReturn(BigDecimal.ZERO);
        Mockito.when(financeService.getBalance(sameTime, sameTime))
                .thenReturn(BigDecimal.ZERO);

        mockMvc.perform(MockMvcRequestBuilders.get("/finance/statistics")
                        .param("start", sameTime.toString())
                        .param("end", sameTime.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalRecords").value(0));
    }
}