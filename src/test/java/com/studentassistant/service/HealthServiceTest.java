package com.studentassistant.service;

import com.studentassistant.dto.HealthDTO;
import com.studentassistant.entity.Health;
import com.studentassistant.repository.HealthRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class HealthServiceTest {

    private HealthRepository healthRepository;
    private HealthService healthService;

    private Health sample;
    private HealthDTO dto;

    @BeforeEach
    void setUp() {
        healthRepository = Mockito.mock(HealthRepository.class);
        healthService = new HealthService(healthRepository);

        dto = new HealthDTO();
        dto.setSleepHours(7.0);
        dto.setExerciseDuration(30);
        dto.setExerciseType("跑步");
        dto.setWaterIntake(1500);
        dto.setMoodScore(8);
        dto.setStressLevel(4);
        dto.setNotes("备注");

        sample = new Health();
        sample.setId(1L);
        sample.setSleepHours(7.0);
        sample.setMoodScore(8);
    }

    @Test
    void testCreateHealth() {
        when(healthRepository.save(any(Health.class))).thenReturn(sample);
        Health result = healthService.createHealth(dto);
        assertEquals(7.0, result.getSleepHours());
    }

    @Test
    void testUpdateHealth() {
        when(healthRepository.findById(1L)).thenReturn(Optional.of(sample));
        when(healthRepository.save(any(Health.class))).thenReturn(sample);
        Health result = healthService.updateHealth(1L, dto);
        assertEquals("跑步", result.getExerciseType());
    }

    @Test
    void testGetAllHealthRecords() {
        when(healthRepository.findAll()).thenReturn(List.of(sample));
        assertEquals(1, healthService.getAllHealthRecords().size());
    }

}