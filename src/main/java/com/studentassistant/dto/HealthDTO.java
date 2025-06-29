package com.studentassistant.dto;

import lombok.Data;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.DecimalMin;

import java.time.LocalDateTime;

@Data
public class HealthDTO {

    private LocalDateTime recordDate;

    @DecimalMin(value = "0.0", message = "睡眠时长不能为负数")
    private Double sleepHours;

    @Min(value = 0, message = "运动时长不能为负数")
    private Integer exerciseDuration;

    private String exerciseType;

    @Min(value = 0, message = "饮水量不能为负数")
    private Integer waterIntake;

    @Min(value = 1, message = "心情评分最小为1")
    @Max(value = 10, message = "心情评分最大为10")
    private Integer moodScore;

    @Min(value = 1, message = "压力等级最小为1")
    @Max(value = 10, message = "压力等级最大为10")
    private Integer stressLevel;

    private String notes;
}