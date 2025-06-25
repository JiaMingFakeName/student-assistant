package com.studentassistant.service;

import com.studentassistant.entity.Finance;
import com.studentassistant.dto.FinanceDTO;
import com.studentassistant.repository.FinanceRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class FinanceService {

    @Autowired
    private FinanceRepository financeRepository;

    public Finance createFinance(FinanceDTO financeDTO) {
        Finance finance = new Finance();
        BeanUtils.copyProperties(financeDTO, finance);
        return financeRepository.save(finance);
    }

    public List<Finance> getAllFinances() {
        return financeRepository.findAll();
    }

    public Optional<Finance> getFinanceById(Long id) {
        return financeRepository.findById(id);
    }

    public Finance updateFinance(Long id, FinanceDTO financeDTO) {
        Finance finance = financeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("财务记录不存在"));

        BeanUtils.copyProperties(financeDTO, finance);
        finance.setId(id);
        return financeRepository.save(finance);
    }

    public void deleteFinance(Long id) {
        financeRepository.deleteById(id);
    }

    public List<Finance> getFinancesByType(String type) {
        return financeRepository.findByType(type);
    }

    public List<Finance> getFinancesByDateRange(LocalDateTime start, LocalDateTime end) {
        return financeRepository.findByTransactionDateBetween(start, end);
    }

    public BigDecimal getTotalAmountByType(String type, LocalDateTime start, LocalDateTime end) {
        BigDecimal total = financeRepository.getTotalAmountByTypeAndDateRange(type, start, end);
        return total != null ? total : BigDecimal.ZERO;
    }

    public BigDecimal getBalance(LocalDateTime start, LocalDateTime end) {
        BigDecimal income = getTotalAmountByType("收入", start, end);
        BigDecimal expense = getTotalAmountByType("支出", start, end);
        return income.subtract(expense);
    }
}