package com.studentassistant.service;

import com.studentassistant.entity.Schedule;
import com.studentassistant.dto.ScheduleDTO;
import com.studentassistant.repository.ScheduleRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ScheduleService {

    @Autowired
    private ScheduleRepository scheduleRepository;

    public Schedule createSchedule(ScheduleDTO scheduleDTO) {
        Schedule schedule = new Schedule();
        BeanUtils.copyProperties(scheduleDTO, schedule);
        return scheduleRepository.save(schedule);
    }

    public List<Schedule> getAllSchedules() {
        return scheduleRepository.findAll();
    }

    public Optional<Schedule> getScheduleById(Long id) {
        return scheduleRepository.findById(id);
    }

    public Schedule updateSchedule(Long id, ScheduleDTO scheduleDTO) {
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

    public List<Schedule> getSchedulesByDateRange(LocalDateTime start, LocalDateTime end) {
        return scheduleRepository.findByStartTimeBetween(start, end);
    }

    public List<Schedule> getTodaySchedules() {
        LocalDateTime today = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0);
        return scheduleRepository.findSchedulesByDate(today);
    }

    public List<Schedule> getSchedulesNeedingReminder() {
        return scheduleRepository.findSchedulesNeedingReminder(LocalDateTime.now());
    }
}