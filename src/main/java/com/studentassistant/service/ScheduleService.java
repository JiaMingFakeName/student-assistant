package com.studentassistant.service;

import com.studentassistant.entity.Schedule;
import com.studentassistant.dto.ScheduleDTO;
import com.studentassistant.repository.ScheduleRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ScheduleService {

    @Autowired
    private ScheduleRepository scheduleRepository;

    public Schedule createSchedule(ScheduleDTO scheduleDTO) {
        // 验证DTO
        validateScheduleDTO(scheduleDTO);

        Schedule schedule = new Schedule();
        BeanUtils.copyProperties(scheduleDTO, schedule);
        try {
            return scheduleRepository.save(schedule);
        } catch (Exception e) {
            // 捕获数据库约束异常
            throw new DataIntegrityViolationException("创建日程失败: " + e.getMessage(), e);
        }
    }

    public List<Schedule> getSchedulesByDateRange(LocalDateTime start, LocalDateTime end) {
        // 验证日期范围
        if (start != null && end != null && start.isAfter(end)) {
            throw new IllegalArgumentException("开始时间不能晚于结束时间");
        }

        return scheduleRepository.findByStartTimeBetween(start, end);
    }

    public List<Schedule> getAllSchedules() {
        return scheduleRepository.findAll();
    }

    public Optional<Schedule> getScheduleById(Long id) {
        return scheduleRepository.findById(id);
    }

    public Schedule updateSchedule(Long id, ScheduleDTO scheduleDTO) {
        // 验证DTO
        validateScheduleDTO(scheduleDTO);

        Schedule schedule = scheduleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("日程记录不存在"));

        BeanUtils.copyProperties(scheduleDTO, schedule);
        schedule.setId(id);
        return scheduleRepository.save(schedule);
    }

    public void deleteSchedule(Long id) {
        scheduleRepository.deleteById(id);
    }

    public List<Schedule> getSchedulesByStatus(String status) {
        return scheduleRepository.findByStatus(status);
    }


    public List<Schedule> getSchedulesForDate(LocalDate date) {
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.plusDays(1).atStartOfDay();
        return scheduleRepository.findSchedulesByDate(startOfDay, endOfDay);
    }

    // 添加这个方法来解决控制器中的调用问题
    public List<Schedule> getTodaySchedules() {
        return getSchedulesForDate(LocalDate.now());
    }

    public List<Schedule> getSchedulesNeedingReminder() {
        return scheduleRepository.findSchedulesNeedingReminder(LocalDateTime.now());
    }

    private void validateScheduleDTO(ScheduleDTO dto) {
        if (dto.getTitle() == null || dto.getTitle().isBlank()) {
            throw new IllegalArgumentException("标题不能为空");
        }
        if (dto.getStartTime() == null) {
            throw new IllegalArgumentException("开始时间不能为空");
        }
        if (dto.getPriority() == null || dto.getPriority().isBlank()) {
            throw new IllegalArgumentException("优先级不能为空");
        }
        if (dto.getStatus() == null || dto.getStatus().isBlank()) {
            throw new IllegalArgumentException("状态不能为空");
        }
        if (dto.getCategory() == null || dto.getCategory().isBlank()) {
            throw new IllegalArgumentException("分类不能为空");
        }
    }
}