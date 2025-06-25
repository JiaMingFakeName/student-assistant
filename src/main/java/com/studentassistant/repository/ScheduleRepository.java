package com.studentassistant.repository;

import com.studentassistant.entity.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ScheduleRepository extends JpaRepository<Schedule, Long> {

    List<Schedule> findByStatus(String status);

    List<Schedule> findByPriority(String priority);

    List<Schedule> findByCategory(String category);

    List<Schedule> findByStartTimeBetween(LocalDateTime start, LocalDateTime end);

    @Query("SELECT s FROM Schedule s WHERE s.reminderTime <= :now AND s.status != '已完成'")
    List<Schedule> findSchedulesNeedingReminder(@Param("now") LocalDateTime now);

    @Query("SELECT s FROM Schedule s WHERE DATE(s.startTime) = DATE(:date)")
    List<Schedule> findSchedulesByDate(@Param("date") LocalDateTime date);
}