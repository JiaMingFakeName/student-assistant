package com.studentassistant.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "health_records")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Health {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "record_date")
    private LocalDateTime recordDate;

    @Column(name = "sleep_hours")
    private Double sleepHours; // 睡眠时长

    @Column(name = "exercise_duration")
    private Integer exerciseDuration; // 运动时长（分钟）

    @Column(name = "exercise_type")
    private String exerciseType; // 运动类型

    @Column(name = "water_intake")
    private Integer waterIntake; // 饮水量（毫升）

    @Column(name = "mood_score")
    private Integer moodScore; // 心情评分 1-10

    @Column(name = "stress_level")
    private Integer stressLevel; // 压力等级 1-10

    @Column(columnDefinition = "TEXT")
    private String notes;

    @Column(name = "created_time")
    private LocalDateTime createdTime;

    @Column(name = "updated_time")
    private LocalDateTime updatedTime;

    @PrePersist
    protected void onCreate() {
        createdTime = LocalDateTime.now();
        updatedTime = LocalDateTime.now();
        if (recordDate == null) {
            recordDate = LocalDateTime.now();
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedTime = LocalDateTime.now();
    }
}