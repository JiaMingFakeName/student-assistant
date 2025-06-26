package com.studentassistant.repository;

import com.studentassistant.entity.Study;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test") // 使用test配置
@Transactional // 确保测试后回滚数据
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class StudyRepositoryTest {

    @Autowired
    private StudyRepository studyRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @Test
    void testFindBySubject() {
        Study study = new Study(null, "数学", "内容", 30, "课程", 3, null, LocalDateTime.now(), LocalDateTime.now());
        studyRepository.save(study);
        studyRepository.flush();
        List<Study> result = studyRepository.findBySubject("数学");
        assertFalse(result.isEmpty());
        assertEquals("数学", result.get(0).getSubject());
    }

    @Test
    void testFindStudyRecordsByDateRange() {
        LocalDateTime now = LocalDateTime.now();
        Study study = new Study(null, "英语", "内容", 40, "作业", 2, null, now.minusDays(1), now.minusDays(1));
        studyRepository.save(study);
        studyRepository.flush();
        List<Study> result = studyRepository.findStudyRecordsByDateRange(now.minusDays(2), now);
        assertFalse(result.isEmpty());
    }

    @Test
    void testGetTotalStudyDurationByDateRange() {
        LocalDateTime start = LocalDateTime.now();

        Study study1 = new Study();
        study1.setSubject("物理");
        study1.setContent("内容");
        study1.setStudyDuration(20);
        study1.setStudyType("复习");
        study1.setDifficultyLevel(1);

        Study study2 = new Study();
        study2.setSubject("物理");
        study2.setContent("内容");
        study2.setStudyDuration(40);
        study2.setStudyType("复习");
        study2.setDifficultyLevel(1);


        studyRepository.saveAndFlush(study1);
        studyRepository.saveAndFlush(study2);
        LocalDateTime end = LocalDateTime.now();

        // Optional: 清除一级缓存，确保查询从数据库读取（不是缓存）
        entityManager.clear();

        Integer total = studyRepository.getTotalStudyDurationByDateRange(start.minusSeconds(1), end.plusSeconds(1));
        assertEquals(60, total);
    }

}