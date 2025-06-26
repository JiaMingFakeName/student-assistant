package com.studentassistant.repository;

import com.studentassistant.entity.Study;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface StudyRepository extends JpaRepository<Study, Long> {

    List<Study> findBySubject(String subject);

    List<Study> findByStudyType(String studyType);

    List<Study> findByCreatedTimeBetween(LocalDateTime start, LocalDateTime end);

    @Query("SELECT s FROM Study s WHERE s.createdTime >= :start AND s.createdTime <= :end")
    List<Study> findStudyRecordsByDateRange(@Param("start") LocalDateTime start,
                                            @Param("end") LocalDateTime end);

    @Query("SELECT COALESCE(SUM(s.studyDuration), 0) FROM Study s WHERE s.createdTime >= :start AND s.createdTime <= :end")
    Integer getTotalStudyDurationByDateRange(@Param("start") LocalDateTime start,
                                             @Param("end") LocalDateTime end);
}