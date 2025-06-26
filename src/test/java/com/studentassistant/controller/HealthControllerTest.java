package com.studentassistant.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.studentassistant.dto.HealthDTO;
import com.studentassistant.entity.Health;
import com.studentassistant.service.HealthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(HealthController.class)
public class HealthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private HealthService healthService;

    @Autowired
    private ObjectMapper objectMapper;

    private Health sampleHealth;
    private HealthDTO sampleDTO;
    private final DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    @BeforeEach
    void setUp() {
        sampleHealth = new Health();
        sampleHealth.setId(1L);
        sampleHealth.setSleepHours(7.5);
        sampleHealth.setMoodScore(8);
        sampleHealth.setExerciseDuration(45);

        sampleDTO = new HealthDTO();
        sampleDTO.setSleepHours(7.5);
        sampleDTO.setMoodScore(8);
        sampleDTO.setExerciseDuration(45);
    }

    @Test
    void testCreateHealth() throws Exception {
        Mockito.when(healthService.createHealth(any(HealthDTO.class))).thenReturn(sampleHealth);

        mockMvc.perform(post("/health")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    void testGetAllHealthRecords() throws Exception {
        Mockito.when(healthService.getAllHealthRecords()).thenReturn(List.of(sampleHealth));

        mockMvc.perform(get("/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L));
    }

    @Test
    void testGetHealthById_Found() throws Exception {
        Mockito.when(healthService.getHealthById(1L)).thenReturn(Optional.of(sampleHealth));

        mockMvc.perform(get("/health/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    void testGetHealthById_NotFound() throws Exception {
        Mockito.when(healthService.getHealthById(9999L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/health/9999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testUpdateHealth_Found() throws Exception {
        Mockito.when(healthService.getHealthById(1L)).thenReturn(Optional.of(sampleHealth));
        Mockito.when(healthService.updateHealth(eq(1L), any(HealthDTO.class))).thenReturn(sampleHealth);

        mockMvc.perform(put("/health/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    void testUpdateHealth_NotFound() throws Exception {
        Mockito.when(healthService.getHealthById(9999L)).thenReturn(Optional.empty());

        mockMvc.perform(put("/health/9999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleDTO)))
                .andExpect(status().isNotFound());
    }

    @Test
    void testDeleteHealth_Found() throws Exception {
        Mockito.when(healthService.getHealthById(1L)).thenReturn(Optional.of(sampleHealth));
        Mockito.doNothing().when(healthService).deleteHealth(1L);
        mockMvc.perform(delete("/health/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Deleted successfully"));
    }

    @Test
    void testDeleteHealth_NotFound() throws Exception {
        Mockito.when(healthService.getHealthById(9999L)).thenReturn(Optional.empty());

        mockMvc.perform(delete("/health/9999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testGetStatistics() throws Exception {
        LocalDateTime start = LocalDateTime.now().minusDays(3);
        LocalDateTime end = LocalDateTime.now();

        String startStr = start.format(formatter);
        String endStr = end.format(formatter);

        Mockito.when(healthService.getHealthByDateRange(start, end)).thenReturn(List.of(sampleHealth));
        Mockito.when(healthService.getAverageSleepHours(start, end)).thenReturn(7.5);
        Mockito.when(healthService.getAverageMoodScore(start, end)).thenReturn(8.0);
        Mockito.when(healthService.getTotalExerciseDuration(start, end)).thenReturn(45);

        mockMvc.perform(get("/health/statistics")
                        .param("start", startStr)
                        .param("end", endStr))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalRecords").value(1))
                .andExpect(jsonPath("$.averageSleepHours").value(7.5))
                .andExpect(jsonPath("$.averageMoodScore").value(8.0))
                .andExpect(jsonPath("$.totalExerciseDuration").value(45));
    }

    @Test
    void testGetStatistics_Empty() throws Exception {
        LocalDateTime start = LocalDateTime.now().minusDays(30);
        LocalDateTime end = LocalDateTime.now();
        String startStr = start.format(formatter);
        String endStr = end.format(formatter);
        Mockito.when(healthService.getHealthByDateRange(start, end)).thenReturn(List.of());
        Mockito.when(healthService.getAverageSleepHours(start, end)).thenReturn(0.0);
        Mockito.when(healthService.getAverageMoodScore(start, end)).thenReturn(0.0);
        Mockito.when(healthService.getTotalExerciseDuration(start, end)).thenReturn(0);
        mockMvc.perform(get("/health/statistics")
                        .param("start", startStr)
                        .param("end", endStr))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalRecords").value(0))
                .andExpect(jsonPath("$.averageSleepHours").value(0.0))
                .andExpect(jsonPath("$.averageMoodScore").value(0.0))
                .andExpect(jsonPath("$.totalExerciseDuration").value(0));
    }
}