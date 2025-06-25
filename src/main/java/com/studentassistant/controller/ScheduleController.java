package com.studentassistant.controller;

import com.studentassistant.entity.Schedule;
import com.studentassistant.dto.ScheduleDTO;
import com.studentassistant.service.ScheduleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/schedule")
public class ScheduleController {

    @Autowired
    private ScheduleService scheduleService;

    @PostMapping
    public ResponseEntity<Schedule> createSchedule(@Valid @RequestBody ScheduleDTO scheduleDTO) {
        Schedule schedule = scheduleService.createSchedule(scheduleDTO);
        return ResponseEntity.ok(schedule);
    }

    @GetMapping
    public ResponseEntity<List<Schedule>> getAllSchedules() {
        List<Schedule> schedules = scheduleService.getAllSchedules();
        return ResponseEntity.ok(schedules);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Schedule> getScheduleById(@PathVariable Long id) {
        return scheduleService.getScheduleById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Schedule> updateSchedule(@PathVariable Long id, @Valid @RequestBody ScheduleDTO scheduleDTO) {
        Schedule schedule = scheduleService.updateSchedule(id, scheduleDTO);
        return ResponseEntity.ok(schedule);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSchedule(@PathVariable Long id) {
        scheduleService.deleteSchedule(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<Schedule>> getSchedulesByStatus(@PathVariable String status) {
        List<Schedule> schedules = scheduleService.getSchedulesByStatus(status);
        return ResponseEntity.ok(schedules);
    }

    @GetMapping("/today")
    public ResponseEntity<List<Schedule>> getTodaySchedules() {
        List<Schedule> schedules = scheduleService.getTodaySchedules();
        return ResponseEntity.ok(schedules);
    }

    @GetMapping("/date-range")
    public ResponseEntity<List<Schedule>> getSchedulesByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {
        List<Schedule> schedules = scheduleService.getSchedulesByDateRange(start, end);
        return ResponseEntity.ok(schedules);
    }

    @GetMapping("/reminders")
    public ResponseEntity<List<Schedule>> getSchedulesNeedingReminder() {
        List<Schedule> schedules = scheduleService.getSchedulesNeedingReminder();
        return ResponseEntity.ok(schedules);
    }
}