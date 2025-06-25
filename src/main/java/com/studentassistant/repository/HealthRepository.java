package com.studentassistant.repository;

import com.studentassistant.entity.Health;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface HealthRepository extends JpaRepository<Health, Long> {

    List<Health> findByRecordDateBetween(LocalDateTime start, LocalDateTime end);

    @Query("SELECT AVG(h.sleepHours) FROM Health h WHERE h.recordDate >= :start AND h.recordDate <= :end")
    Double getAverageSleepHours(@Param("start") LocalDateTime start,
                                @Param("end") LocalDateTime end);

    @Query("SELECT AVG(h.moodScore) FROM Health h WHERE h.recordDate >= :start AND h.recordDate <= :end")
    Double getAverageMoodScore(@Param("start") LocalDateTime start,
                               @Param("end") LocalDateTime end);

    @Query("SELECT SUM(h.exerciseDuration) FROM Health h WHERE h.recordDate >= :start AND h.recordDate <= :end")
    Integer getTotalExerciseDuration(@Param("start") LocalDateTime start,
                                     @Param("end") LocalDateTime end);
}