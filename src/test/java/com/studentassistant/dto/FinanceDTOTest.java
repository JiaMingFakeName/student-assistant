// FinanceDTOTest.java
package com.studentassistant.dto;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class FinanceDTOTest {

    private static Validator validator;

    @BeforeAll
    static void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    // DT01: 空值约束验证
    @Test
    void validation_shouldFailOnNullFields() {
        FinanceDTO dto = new FinanceDTO();

        var violations = validator.validate(dto);

        assertEquals(4, violations.size()); // title, amount, type, category
    }

    // DT02: 最小值约束验证
    @Test
    void amountValidation_shouldFailBelowMin() {
        FinanceDTO dto = new FinanceDTO();
        dto.setTitle("Test");
        dto.setAmount(new BigDecimal("0.00")); // 低于最小值
        dto.setType("支出");
        dto.setCategory("测试");

        var violations = validator.validate(dto);

        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
    }

    // DT03: 有效DTO验证
    @Test
    void validDTO_shouldPassValidation() {
        FinanceDTO dto = new FinanceDTO();
        dto.setTitle("Valid Test");
        dto.setAmount(new BigDecimal("50.0"));
        dto.setType("收入");
        dto.setCategory("奖金");
        dto.setDescription("Valid description");

        var violations = validator.validate(dto);

        assertTrue(violations.isEmpty());
    }
}