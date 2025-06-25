package com.studentassistant.service;

import com.studentassistant.entity.Health;
import com.studentassistant.dto.HealthDTO;
import com.studentassistant.repository.HealthRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class HealthService {

    @Autowired
    private HealthRepository healthRepository;

    public Health createHealth(HealthDTO healthDTO) {
        Health health = new Health();
        BeanUtils.copyProperties(healthDTO, health);
        return healthRepository.save(health);
    }

    public List<Health> getAllHealthRecords() {
        return healthRepository.findAll();
    }

    public Optional<Health> getHealthById(Long id) {
        return healthRepository.findById(id);
    }

    public Health updateHealth(Long id, HealthDTO healthDTO) {
        Health health = healthRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("健康记录不存在"));

        BeanUtils.copyProperties(healthDTO, health);
        health.setId(id);
        return healthRepository.save(health);
    }

    public void deleteHealth(Long id) {
        healthRepository.deleteById(id);
    }

    public List<Health> getHealthByDateRange(LocalDateTime start, LocalDateTime end) {
        return healthRepository.findByRecordDateBetween(start, end);
    }

    public Double getAverageSleepHours(LocalDateTime start, LocalDateTime end) {
        Double avg = healthRepository.getAverageSleepHours(start, end);
        return avg != null ? avg : 0.0;
    }

    public Double getAverageMoodScore(LocalDateTime start, LocalDateTime end) {
        Double avg = healthRepository.getAverageMoodScore(start, end);
        return avg != null ? avg : 0.0;
    }

    public Integer getTotalExerciseDuration(LocalDateTime start, LocalDateTime end) {
        Integer total = healthRepository.getTotalExerciseDuration(start, end);
        return total != null ? total : 0;
    }
}