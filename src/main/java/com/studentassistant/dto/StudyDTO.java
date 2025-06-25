package com.studentassistant.dto;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;

@Data
public class StudyDTO {

    @NotBlank(message = "科目不能为空")
    private String subject;

    @NotBlank(message = "学习内容不能为空")
    private String content;

    @NotNull(message = "学习时长不能为空")
    @Min(value = 1, message = "学习时长至少1分钟")
    private Integer studyDuration;

    @NotBlank(message = "学习类型不能为空")
    private String studyType;

    @Min(value = 1, message = "难度等级最小为1")
    @Max(value = 5, message = "难度等级最大为5")
    private Integer difficultyLevel;

    private String notes;
}