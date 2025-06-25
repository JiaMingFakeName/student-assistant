package com.studentassistant.dto;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

@Data
public class ScheduleDTO {

    @NotBlank(message = "标题不能为空")
    private String title;

    private String description;

    @NotNull(message = "开始时间不能为空")
    private LocalDateTime startTime;

    private LocalDateTime endTime;

    @NotBlank(message = "优先级不能为空")
    private String priority;

    @NotBlank(message = "状态不能为空")
    private String status;

    @NotBlank(message = "分类不能为空")
    private String category;

    private LocalDateTime reminderTime;
}