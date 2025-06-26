package com.studentassistant.controller;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.studentassistant.dto.ScheduleDTO;
import com.studentassistant.entity.Schedule;
import com.studentassistant.service.ScheduleService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class ScheduleControllerTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private ScheduleService scheduleService;

    @InjectMocks
    private ScheduleController scheduleController;

    private ScheduleDTO validDTO;
    private Schedule validSchedule;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(scheduleController).build();

        // 注册JavaTimeModule解决LocalDateTime序列化问题
        objectMapper.registerModule(new JavaTimeModule());

        validDTO = new ScheduleDTO();
        validDTO.setTitle("API 测试");
        validDTO.setStartTime(LocalDateTime.now().plusHours(3));
        validDTO.setPriority("中");
        validDTO.setStatus("待办");
        validDTO.setCategory("测试");

        validSchedule = new Schedule();
        validSchedule.setId(1L);
        validSchedule.setTitle("API 测试");
        validSchedule.setStartTime(LocalDateTime.now().plusHours(3));
        validSchedule.setPriority("中");
        validSchedule.setStatus("待办");
        validSchedule.setCategory("测试");
    }

    // 创建日程端点测试
    @Test
    void createSchedule_ValidInput_ShouldReturn201() throws Exception {
        given(scheduleService.createSchedule(any(ScheduleDTO.class))).willReturn(validSchedule);

        mockMvc.perform(post("/schedule")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("API 测试"));
    }

    @Test
    void createSchedule_InvalidInput_ShouldReturn400() throws Exception {
        ScheduleDTO invalidDTO = new ScheduleDTO(); // 缺少必填字段

        mockMvc.perform(post("/schedule")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDTO)))
                .andExpect(status().isBadRequest());
    }

    // 获取日程端点测试
    @Test
    void getScheduleById_ExistingId_ShouldReturn200() throws Exception {
        given(scheduleService.getScheduleById(1L)).willReturn(Optional.of(validSchedule));

        mockMvc.perform(get("/schedule/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void getScheduleById_NonExistingId_ShouldReturn404() throws Exception {
        given(scheduleService.getScheduleById(999L)).willReturn(Optional.empty());

        mockMvc.perform(get("/schedule/999"))
                .andExpect(status().isNotFound());
    }

    // 更新日程端点测试
    @Test
    void updateSchedule_ValidInput_ShouldReturn200() throws Exception {
        given(scheduleService.updateSchedule(eq(1L), any(ScheduleDTO.class))).willReturn(validSchedule);

        mockMvc.perform(put("/schedule/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("API 测试"));
    }

    // 删除日程端点测试
    @Test
    void deleteSchedule_ExistingId_ShouldReturn200() throws Exception {
        willDoNothing().given(scheduleService).deleteSchedule(1L);

        mockMvc.perform(delete("/schedule/1"))
                .andExpect(status().isOk());
    }

    // 状态查询端点测试
    @Test
    void getSchedulesByStatus_ValidStatus_ShouldReturn200() throws Exception {
        given(scheduleService.getSchedulesByStatus("待办")).willReturn(Collections.singletonList(validSchedule));

        mockMvc.perform(get("/schedule/status/待办"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].status").value("待办"));
    }

    // 今日日程端点测试
    @Test
    void getTodaySchedules_ShouldReturn200() throws Exception {
        given(scheduleService.getTodaySchedules()).willReturn(Collections.singletonList(validSchedule));

        mockMvc.perform(get("/schedule/today"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("API 测试"));
    }

    // 日期范围查询端点测试
    @Test
    void getSchedulesByDateRange_ValidRange_ShouldReturn200() throws Exception {
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = LocalDateTime.now().plusDays(7);

        given(scheduleService.getSchedulesByDateRange(start, end))
                .willReturn(Collections.singletonList(validSchedule));

        mockMvc.perform(get("/schedule/date-range")
                        .param("start", start.toString())
                        .param("end", end.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1));
    }

    // 需要提醒的日程端点测试
    @Test
    void getSchedulesNeedingReminder_ShouldReturn200() throws Exception {
        given(scheduleService.getSchedulesNeedingReminder()).willReturn(Collections.singletonList(validSchedule));

        mockMvc.perform(get("/schedule/reminders"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("API 测试"));
    }
}