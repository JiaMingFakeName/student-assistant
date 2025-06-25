package com.studentassistant.dto;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.DecimalMin;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class FinanceDTO {

    @NotBlank(message = "标题不能为空")
    private String title;

    @NotNull(message = "金额不能为空")
    @DecimalMin(value = "0.01", message = "金额必须大于0")
    private BigDecimal amount;

    @NotBlank(message = "类型不能为空")
    private String type;

    @NotBlank(message = "分类不能为空")
    private String category;

    private String description;

    private LocalDateTime transactionDate;
}