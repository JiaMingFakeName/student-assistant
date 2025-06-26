package com.studentassistant.controller;

import com.studentassistant.entity.Health;
import com.studentassistant.dto.HealthDTO;
import com.studentassistant.service.HealthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/health")
public class HealthController {

    private final HealthService healthService;

    @Autowired
    public HealthController(HealthService healthService) {
        this.healthService = healthService;
    }

    @PostMapping
    public ResponseEntity<Health> createHealth(@Valid @RequestBody HealthDTO healthDTO) {
        Health health = healthService.createHealth(healthDTO);
        return ResponseEntity.ok(health);
    }

    @GetMapping
    public ResponseEntity<List<Health>> getAllHealthRecords() {
        List<Health> healthRecords = healthService.getAllHealthRecords();
        return ResponseEntity.ok(healthRecords);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Health> getHealthById(@PathVariable Long id) {
        return healthService.getHealthById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Health> updateHealth(@PathVariable Long id, @Valid @RequestBody HealthDTO healthDTO) {
        return healthService.getHealthById(id).map(existing -> {
            Health updated = healthService.updateHealth(id, healthDTO);
            return ResponseEntity.ok(updated);
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteHealth(@PathVariable Long id) {
        if (healthService.getHealthById(id).isEmpty()) {
            return ResponseEntity.status(404).body(Map.of("message", "Health record not found"));
        }
        healthService.deleteHealth(id);
        return ResponseEntity.ok(Map.of("message", "Deleted successfully"));
    }

    @GetMapping("/statistics")
    public ResponseEntity<Map<String, Object>> getHealthStatistics(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {

        List<Health> healthRecords = healthService.getHealthByDateRange(start, end);
        Double avgSleepHours = healthService.getAverageSleepHours(start, end);
        Double avgMoodScore = healthService.getAverageMoodScore(start, end);
        Integer totalExerciseDuration = healthService.getTotalExerciseDuration(start, end);

        Map<String, Object> statistics = new HashMap<>();
        statistics.put("totalRecords", healthRecords.size());
        statistics.put("averageSleepHours", avgSleepHours);
        statistics.put("averageMoodScore", avgMoodScore);
        statistics.put("totalExerciseDuration", totalExerciseDuration);
        statistics.put("records", healthRecords);

        return ResponseEntity.ok(statistics);
    }
}

