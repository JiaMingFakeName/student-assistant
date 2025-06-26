package com.studentassistant.repository;

import com.studentassistant.entity.Health;
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
public class HealthRepositoryTest {

    @Autowired
    private HealthRepository healthRepository;

    @Test
    void testSaveAndFind() {
        Health h = new Health();
        h.setSleepHours(8.0);
        h.setMoodScore(9);
        h.setRecordDate(LocalDateTime.now());

        healthRepository.save(h);
        List<Health> all = healthRepository.findAll();
        assertFalse(all.isEmpty());
    }

    @Test
    void testFindByRecordDateBetween() {
        Health h1 = new Health();
        h1.setRecordDate(LocalDateTime.now().minusDays(1));
        h1.setSleepHours(7.5);

        Health h2 = new Health();
        h2.setRecordDate(LocalDateTime.now());
        h2.setSleepHours(6.5);

        healthRepository.saveAll(List.of(h1, h2));

        List<Health> result = healthRepository.findByRecordDateBetween(LocalDateTime.now().minusDays(2), LocalDateTime.now().plusDays(1));
        assertEquals(2, result.size());
    }

    @Test
    void testDeleteById() {
        Health h = new Health();
        h.setRecordDate(LocalDateTime.now());
        h = healthRepository.save(h);
        healthRepository.deleteById(h.getId());
        assertFalse(healthRepository.findById(h.getId()).isPresent());
    }

    @Test
    void testUpdateSleepHours() {
        Health h = new Health();
        h.setRecordDate(LocalDateTime.now());
        h.setSleepHours(5.0);
        Health saved = healthRepository.save(h);

        saved.setSleepHours(7.5);
        Health updated = healthRepository.save(saved);

        assertEquals(7.5, updated.getSleepHours());
    }
}
