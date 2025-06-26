package com.studentassistant.repository;

import com.studentassistant.entity.Schedule;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test") // 使用test配置
@Transactional // 确保测试后回滚数据
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class ScheduleRepositoryTest {

    @Autowired
    private ScheduleRepository repository;

    @Test
    void findByStatus_ShouldReturnMatchingRecords() {
        // Given
        Schedule todo = saveSchedule("待办");
        Schedule done = saveSchedule("已完成");

        // When
        List<Schedule> results = repository.findByStatus("待办");

        // Then
        assertEquals(1, results.size());
        assertEquals("待办", results.get(0).getStatus());
    }

    @Test
    void findByPriority_ShouldReturnMatchingRecords() {
        // Given
        Schedule high = saveScheduleWithPriority("高");
        Schedule low = saveScheduleWithPriority("低");

        // When
        List<Schedule> results = repository.findByPriority("高");

        // Then
        assertEquals(1, results.size());
        assertEquals("高", results.get(0).getPriority());
    }

    @Test
    void findByCategory_ShouldReturnMatchingRecords() {
        // Given
        Schedule study = saveScheduleWithCategory("学习");
        Schedule life = saveScheduleWithCategory("生活");

        // When
        List<Schedule> results = repository.findByCategory("学习");

        // Then
        assertEquals(1, results.size());
        assertEquals("学习", results.get(0).getCategory());
    }

    @Test
    void findByStartTimeBetween_ShouldReturnInRangeRecords() {
        // Given
        LocalDateTime now = LocalDateTime.now();
        Schedule inRange = saveScheduleWithTime(now.plusHours(1));
        Schedule outOfRange = saveScheduleWithTime(now.plusDays(2));

        // When
        List<Schedule> results = repository.findByStartTimeBetween(
                now,
                now.plusDays(1)
        );

        // Then
        assertEquals(1, results.size());
        assertEquals(inRange.getId(), results.get(0).getId());
    }

    @Test
    void findSchedulesNeedingReminder_ShouldReturnValidRecords() {
        // Given
        LocalDateTime now = LocalDateTime.now();
        Schedule needsReminder = saveScheduleWithReminder(now.minusMinutes(5), "待办");
        Schedule completed = saveScheduleWithReminder(now.minusMinutes(5), "已完成");
        Schedule futureReminder = saveScheduleWithReminder(now.plusMinutes(5), "待办");

        // When
        List<Schedule> results = repository.findSchedulesNeedingReminder(now);

        // Then
        assertEquals(1, results.size());
        assertEquals(needsReminder.getId(), results.get(0).getId());
    }

    @Test
    void findSchedulesByDate_ShouldReturnSameDayRecords() {
        // Given
        LocalDate today = LocalDate.now();
        LocalDateTime startOfDay = today.atStartOfDay();
        LocalDateTime endOfDay = today.plusDays(1).atStartOfDay();

        Schedule todaySchedule = saveScheduleWithTime(startOfDay.plusHours(9));
        Schedule tomorrowSchedule = saveScheduleWithTime(startOfDay.plusDays(1));

        // When
        List<Schedule> results = repository.findSchedulesByDate(startOfDay, endOfDay);

        // Then
        assertEquals(1, results.size());
        assertEquals(todaySchedule.getId(), results.get(0).getId());
    }

    // Helper methods
    private Schedule saveSchedule(String status) {
        Schedule schedule = new Schedule();
        schedule.setTitle("Test");
        schedule.setStatus(status);
        schedule.setPriority("中");
        schedule.setCategory("其他");
        schedule.setStartTime(LocalDateTime.now());
        return repository.save(schedule);
    }

    private Schedule saveScheduleWithPriority(String priority) {
        Schedule schedule = new Schedule();
        schedule.setTitle("Test");
        schedule.setStatus("待办");
        schedule.setPriority(priority);
        schedule.setCategory("其他");
        schedule.setStartTime(LocalDateTime.now());
        return repository.save(schedule);
    }

    private Schedule saveScheduleWithCategory(String category) {
        Schedule schedule = new Schedule();
        schedule.setTitle("Test");
        schedule.setStatus("待办");
        schedule.setPriority("中");
        schedule.setCategory(category);
        schedule.setStartTime(LocalDateTime.now());
        return repository.save(schedule);
    }

    private Schedule saveScheduleWithTime(LocalDateTime startTime) {
        Schedule schedule = new Schedule();
        schedule.setTitle("Test");
        schedule.setStatus("待办");
        schedule.setPriority("中");
        schedule.setCategory("其他");
        schedule.setStartTime(startTime);
        return repository.save(schedule);
    }

    private Schedule saveScheduleWithReminder(LocalDateTime reminderTime, String status) {
        Schedule schedule = new Schedule();
        schedule.setTitle("Test");
        schedule.setStatus(status);
        schedule.setPriority("中");
        schedule.setCategory("其他");
        schedule.setStartTime(LocalDateTime.now().plusHours(1));
        schedule.setReminderTime(reminderTime);
        return repository.save(schedule);
    }
}