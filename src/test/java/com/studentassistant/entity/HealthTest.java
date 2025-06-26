package com.studentassistant.entity;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class HealthTest {

    @Test
    void testNoArgsConstructor() {
        Health health = new Health();
        assertNotNull(health);
    }

    @Test
    void testAllArgsConstructor() {
        LocalDateTime now = LocalDateTime.now();
        Health health = new Health(1L, now, 8.0, 30, "跑步", 1500, 9, 4, "Good", now, now);
        assertEquals(1L, health.getId());
        assertEquals("跑步", health.getExerciseType());
    }

    @Test
    void testOnCreate() {
        Health health = new Health();
        health.onCreate();
        assertNotNull(health.getCreatedTime());
        assertNotNull(health.getUpdatedTime());
        assertNotNull(health.getRecordDate());
    }

    @Test
    void testOnUpdate() {
        Health health = new Health();
        health.onUpdate();
        assertNotNull(health.getUpdatedTime());
    }
}
