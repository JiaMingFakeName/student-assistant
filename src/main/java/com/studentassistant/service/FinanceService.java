package com.studentassistant.service;

import com.studentassistant.entity.Finance;
import com.studentassistant.dto.FinanceDTO;
import com.studentassistant.repository.FinanceRepository;
import jakarta.persistence.EntityNotFoundException;
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
        // 手动触发时间戳设置
        finance.onCreate(); // 新增此行
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
                .orElseThrow(() -> new EntityNotFoundException("财务记录不存在")); // 修改为 JPA 标准异常
        BeanUtils.copyProperties(financeDTO, finance);
        finance.setId(id);
        // 手动触发更新时间戳
        finance.onUpdate(); // 新增此行
        return financeRepository.save(finance);
    }

    public void deleteFinance(Long id) {
        if (!financeRepository.existsById(id)) {
            throw new EntityNotFoundException("财务记录不存在");
        }
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