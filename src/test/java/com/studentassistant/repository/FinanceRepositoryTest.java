// FinanceRepositoryTest.java
package com.studentassistant.repository;

import com.studentassistant.entity.Finance;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test") // 使用test配置
@Transactional // 确保测试后回滚数据
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class FinanceRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private FinanceRepository financeRepository;

    // RT01: 简单查询验证
    @Test
    void findByType_shouldReturnCorrectRecords() {
        Finance income = new Finance(null, "Salary", new BigDecimal("5000"), "收入", "工资",
                null, LocalDateTime.now(), null, null);
        Finance expense = new Finance(null, "Rent", new BigDecimal("1500"), "支出", "住房",
                null, LocalDateTime.now(), null, null);

        entityManager.persist(income);
        entityManager.persist(expense);
        entityManager.flush();

        List<Finance> incomes = financeRepository.findByType("收入");
        assertEquals(1, incomes.size());
        assertEquals("Salary", incomes.get(0).getTitle());
    }

    // RT02: 聚合函数测试
    @Test
    void getTotalAmountByTypeAndDateRange_shouldSumCorrectly() {
        LocalDateTime start = LocalDateTime.of(2025, 1, 1, 0, 0);
        LocalDateTime end = LocalDateTime.of(2025, 1, 10, 23, 59);

        Finance f1 = new Finance(null, "Food", new BigDecimal("100"), "支出", "餐饮",
                null, LocalDateTime.of(2025, 1, 5, 12, 0), null, null);
        Finance f2 = new Finance(null, "Transport", new BigDecimal("50"), "支出", "交通",
                null, LocalDateTime.of(2025, 1, 8, 9, 30), null, null);
        Finance f3 = new Finance(null, "Bonus", new BigDecimal("1000"), "收入", "奖金",
                null, LocalDateTime.of(2025, 2, 1, 0, 0), null, null); // 超出范围

        entityManager.persist(f1);
        entityManager.persist(f2);
        entityManager.persist(f3);
        entityManager.flush();

        BigDecimal total = financeRepository.getTotalAmountByTypeAndDateRange("支出", start, end);
        assertEquals(0, new BigDecimal("150").compareTo(total));
    }

    // RT03: 空结果集处理
    @Test
    void getExpenseGroupByCategory_noData_shouldReturnEmpty() {
        LocalDateTime start = LocalDateTime.now().minusDays(30);
        LocalDateTime end = LocalDateTime.now();

        List<Object[]> result = financeRepository.getExpenseGroupByCategory(start, end);
        assertTrue(result.isEmpty());
    }

    // RT04: 时间范围验证
    @Test
    void findByTransactionDateBetween_shouldFilterByDate() {
        LocalDateTime dayStart = LocalDateTime.of(2025, 3, 1, 0, 0);
        LocalDateTime dayEnd = LocalDateTime.of(2025, 3, 1, 23, 59);

        Finance inRange = new Finance(null, "Morning Coffee", new BigDecimal("20"), "支出", "餐饮",
                null, LocalDateTime.of(2025, 3, 1, 8, 30), null, null);
        Finance outOfRange = new Finance(null, "Dinner", new BigDecimal("80"), "支出", "餐饮",
                null, LocalDateTime.of(2025, 3, 2, 19, 0), null, null);

        entityManager.persist(inRange);
        entityManager.persist(outOfRange);
        entityManager.flush();

        List<Finance> results = financeRepository.findByTransactionDateBetween(dayStart, dayEnd);
        assertEquals(1, results.size());
        assertEquals("Morning Coffee", results.get(0).getTitle());
    }
}