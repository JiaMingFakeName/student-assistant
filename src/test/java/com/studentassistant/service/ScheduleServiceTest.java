package com.studentassistant.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.studentassistant.dto.ScheduleDTO;
import com.studentassistant.entity.Schedule;
import com.studentassistant.repository.ScheduleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class ScheduleServiceTest {

    @Mock
    private ScheduleRepository scheduleRepository;

    @InjectMocks
    private ScheduleService scheduleService;

    private ScheduleDTO validDTO;
    private Schedule existingSchedule;

    @BeforeEach
    void setUp() {
        validDTO = new ScheduleDTO();
        validDTO.setTitle("项目会议");
        validDTO.setStartTime(LocalDateTime.now().plusHours(2));
        validDTO.setPriority("高");
        validDTO.setStatus("待办");
        validDTO.setCategory("学习");

        existingSchedule = new Schedule();
        existingSchedule.setId(1L);
        existingSchedule.setTitle("项目会议");
        existingSchedule.setStartTime(LocalDateTime.now().plusHours(2));
        existingSchedule.setPriority("高");
        existingSchedule.setStatus("待办");
        existingSchedule.setCategory("学习");
    }

    // 创建日程测试
    @Test
    void createSchedule_WithValidData_ShouldReturnSchedule() {
        when(scheduleRepository.save(any(Schedule.class))).thenReturn(existingSchedule);

        Schedule result = scheduleService.createSchedule(validDTO);

        assertNotNull(result);
        assertEquals("项目会议", result.getTitle());
        verify(scheduleRepository, times(1)).save(any(Schedule.class));
    }

    @Test
    void createSchedule_WithMissingRequiredFields_ShouldThrowException() {
        ScheduleDTO invalidDTO = new ScheduleDTO(); // 缺少必填字段

        // 1. 验证服务层手动验证（缺少必填字段）
        IllegalArgumentException validationException = assertThrows(
                IllegalArgumentException.class,
                () -> scheduleService.createSchedule(invalidDTO)
        );
        assertTrue(validationException.getMessage().contains("不能为空"));

        // 2. 验证数据库异常（使用完全有效的DTO）
        // 创建一个完全有效的DTO
        ScheduleDTO validDTOForDBTest = new ScheduleDTO();
        validDTOForDBTest.setTitle("测试标题");
        validDTOForDBTest.setStartTime(LocalDateTime.now());
        validDTOForDBTest.setPriority("中");
        validDTOForDBTest.setStatus("进行中");
        validDTOForDBTest.setCategory("学习"); // 确保所有字段有效

        // 模拟数据库保存时抛出异常
        when(scheduleRepository.save(any(Schedule.class)))
                .thenThrow(new DataIntegrityViolationException("数据库约束失败"));

        DataIntegrityViolationException dbException = assertThrows(
                DataIntegrityViolationException.class,
                () -> scheduleService.createSchedule(validDTOForDBTest)
        );
        assertTrue(dbException.getMessage().contains("数据库约束失败"));
    }

    // 更新日程测试
    @Test
    void updateSchedule_ExistingId_ShouldUpdateSchedule() {
        when(scheduleRepository.findById(1L)).thenReturn(Optional.of(existingSchedule));
        when(scheduleRepository.save(any(Schedule.class))).thenReturn(existingSchedule);

        validDTO.setTitle("更新后的会议");
        Schedule result = scheduleService.updateSchedule(1L, validDTO);

        assertEquals("更新后的会议", result.getTitle());
        verify(scheduleRepository, times(1)).save(any(Schedule.class));
    }

    @Test
    void updateSchedule_NonExistingId_ShouldThrowException() {
        when(scheduleRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> {
            scheduleService.updateSchedule(999L, validDTO);
        });
    }

    // 查询测试
    @Test
    void getAllSchedules_ShouldReturnAllSchedules() {
        Schedule schedule2 = new Schedule();
        schedule2.setId(2L);
        when(scheduleRepository.findAll()).thenReturn(Arrays.asList(existingSchedule, schedule2));

        List<Schedule> result = scheduleService.getAllSchedules();

        assertEquals(2, result.size());
    }

    @Test
    void getScheduleById_ExistingId_ShouldReturnSchedule() {
        when(scheduleRepository.findById(1L)).thenReturn(Optional.of(existingSchedule));

        Optional<Schedule> result = scheduleService.getScheduleById(1L);

        assertTrue(result.isPresent());
        assertEquals("项目会议", result.get().getTitle());
    }

    @Test
    void getScheduleById_NonExistingId_ShouldReturnEmpty() {
        when(scheduleRepository.findById(999L)).thenReturn(Optional.empty());

        Optional<Schedule> result = scheduleService.getScheduleById(999L);

        assertTrue(result.isEmpty());
    }

    // 按状态查询测试
    @Test
    void getSchedulesByStatus_ValidStatus_ShouldReturnSchedules() {
        when(scheduleRepository.findByStatus("待办")).thenReturn(Collections.singletonList(existingSchedule));

        List<Schedule> result = scheduleService.getSchedulesByStatus("待办");

        assertEquals(1, result.size());
        assertEquals("待办", result.get(0).getStatus());
    }

    // 按日期查询测试
    @Test
    void getSchedulesForDate_ShouldReturnSchedules() {
        // 准备测试数据
        LocalDate date = LocalDate.of(2023, 6, 15);
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.plusDays(1).atStartOfDay();

        // 正确模拟双参数调用
        when(scheduleRepository.findSchedulesByDate(startOfDay, endOfDay))
                .thenReturn(Collections.singletonList(existingSchedule));

        // 执行测试
        List<Schedule> result = scheduleService.getSchedulesForDate(date);

        // 验证结果
        assertEquals(1, result.size());
        assertEquals("项目会议", result.get(0).getTitle());
    }

    // 今日日程测试
    @Test
    void getTodaySchedules_ShouldReturnTodaySchedules() {
        // 准备测试数据
        LocalDate today = LocalDate.now();
        LocalDateTime startOfDay = today.atStartOfDay();
        LocalDateTime endOfDay = today.plusDays(1).atStartOfDay();

        // 正确模拟双参数调用
        when(scheduleRepository.findSchedulesByDate(startOfDay, endOfDay))
                .thenReturn(Collections.singletonList(existingSchedule));

        // 执行测试
        List<Schedule> result = scheduleService.getTodaySchedules();

        // 验证结果
        assertEquals(1, result.size());
    }

    // 需要提醒的日程测试
    @Test
    void getSchedulesNeedingReminder_ShouldReturnSchedules() {
        Schedule reminderSchedule = new Schedule();
        reminderSchedule.setReminderTime(LocalDateTime.now().minusMinutes(30));
        reminderSchedule.setStatus("待办");

        when(scheduleRepository.findSchedulesNeedingReminder(any(LocalDateTime.class)))
                .thenReturn(Collections.singletonList(reminderSchedule));

        List<Schedule> result = scheduleService.getSchedulesNeedingReminder();

        assertEquals(1, result.size());
        assertNotNull(result.get(0).getReminderTime());
    }

    // 边界测试：日期范围查询
    @Test
    void getSchedulesByDateRange_ValidRange_ShouldReturnSchedules() {
        LocalDateTime start = LocalDateTime.now().minusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(1);

        when(scheduleRepository.findByStartTimeBetween(start, end))
                .thenReturn(Collections.singletonList(existingSchedule));

        List<Schedule> result = scheduleService.getSchedulesByDateRange(start, end);

        assertEquals(1, result.size());
    }

    @Test
    void getSchedulesByDateRange_InvalidRange_ShouldThrowException() {
        LocalDateTime start = LocalDateTime.now().plusDays(2);
        LocalDateTime end = LocalDateTime.now().plusDays(1); // 结束早于开始

        // 验证抛出正确异常
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> scheduleService.getSchedulesByDateRange(start, end)
        );

        // 验证错误消息内容
        assertTrue(exception.getMessage().contains("开始时间不能晚于结束时间"));

        // 确保没有调用 repository
        verify(scheduleRepository, never()).findByStartTimeBetween(any(), any());
    }
}