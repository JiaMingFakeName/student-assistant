package com.studentassistant.controller;

import com.studentassistant.entity.Finance;
import com.studentassistant.dto.FinanceDTO;
import com.studentassistant.service.FinanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/finance")
public class FinanceController {

    @Autowired
    private FinanceService financeService;

    @PostMapping
    public ResponseEntity<Finance> createFinance(@Valid @RequestBody FinanceDTO financeDTO) {
        Finance finance = financeService.createFinance(financeDTO);
        return ResponseEntity.ok(finance);
    }

    @GetMapping
    public ResponseEntity<List<Finance>> getAllFinances() {
        List<Finance> finances = financeService.getAllFinances();
        return ResponseEntity.ok(finances);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Finance> getFinanceById(@PathVariable Long id) {
        return financeService.getFinanceById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Finance> updateFinance(@PathVariable Long id, @Valid @RequestBody FinanceDTO financeDTO) {
        Finance finance = financeService.updateFinance(id, financeDTO);
        return ResponseEntity.ok(finance);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFinance(@PathVariable Long id) {
        financeService.deleteFinance(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/type/{type}")
    public ResponseEntity<List<Finance>> getFinancesByType(@PathVariable String type) {
        List<Finance> finances = financeService.getFinancesByType(type);
        return ResponseEntity.ok(finances);
    }

    @GetMapping("/statistics")
    public ResponseEntity<Map<String, Object>> getFinanceStatistics(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {

        List<Finance> finances = financeService.getFinancesByDateRange(start, end);
        BigDecimal totalIncome = financeService.getTotalAmountByType("收入", start, end);
        BigDecimal totalExpense = financeService.getTotalAmountByType("支出", start, end);
        BigDecimal balance = financeService.getBalance(start, end);

        Map<String, Object> statistics = new HashMap<>();
        statistics.put("totalRecords", finances.size());
        statistics.put("totalIncome", totalIncome);
        statistics.put("totalExpense", totalExpense);
        statistics.put("balance", balance);
        statistics.put("records", finances);

        return ResponseEntity.ok(statistics);
    }
}