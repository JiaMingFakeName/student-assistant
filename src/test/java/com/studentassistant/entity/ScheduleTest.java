package com.studentassistant.entity;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import static org.junit.jupiter.api.Assertions.*;

class ScheduleTest {

    @Test
    void prePersist_ShouldSetCreatedAndUpdatedTime() {
        Schedule schedule = new Schedule();
        schedule.setTitle("Test");

        // 记录测试开始时间
        LocalDateTime testStart = LocalDateTime.now();

        // 触发@PrePersist
        schedule.onCreate();

        // 记录测试结束时间
        LocalDateTime testEnd = LocalDateTime.now();

        assertNotNull(schedule.getCreatedTime());
        assertNotNull(schedule.getUpdatedTime());

        // 验证时间在测试开始和结束时间范围内
        assertFalse(schedule.getCreatedTime().isBefore(testStart));
        assertFalse(schedule.getCreatedTime().isAfter(testEnd));
        assertFalse(schedule.getUpdatedTime().isBefore(testStart));
        assertFalse(schedule.getUpdatedTime().isAfter(testEnd));

        // 验证创建时间和更新时间非常接近（允许1秒内的差异）
        long diff = Math.abs(ChronoUnit.MILLIS.between(
                schedule.getCreatedTime(),
                schedule.getUpdatedTime()
        ));
        assertTrue(diff < 1000, "创建时间和更新时间应非常接近");
    }

    @Test
    void preUpdate_ShouldUpdateOnlyUpdatedTime() {
        Schedule schedule = new Schedule();
        schedule.onCreate();

        LocalDateTime originalCreatedTime = schedule.getCreatedTime();
        LocalDateTime originalUpdatedTime = schedule.getUpdatedTime();

        // 模拟更新
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        schedule.onUpdate();

        assertEquals(originalCreatedTime, schedule.getCreatedTime());
        assertNotEquals(originalUpdatedTime, schedule.getUpdatedTime());
        assertTrue(schedule.getUpdatedTime().isAfter(originalUpdatedTime));
    }

    @Test
    void entityFields_ShouldBeSetCorrectly() {
        LocalDateTime now = LocalDateTime.now();
        Schedule schedule = new Schedule(
                1L, "Project", "Description", now, now.plusHours(2),
                "高", "待办", "学习", now.minusMinutes(30), null, null
        );

        assertEquals("Project", schedule.getTitle());
        assertEquals("Description", schedule.getDescription());
        assertEquals(now, schedule.getStartTime());
        assertEquals(now.plusHours(2), schedule.getEndTime());
        assertEquals("高", schedule.getPriority());
        assertEquals("待办", schedule.getStatus());
        assertEquals("学习", schedule.getCategory());
        assertEquals(now.minusMinutes(30), schedule.getReminderTime());
    }

    @Test
    void noArgsConstructor_ShouldCreateEmptyObject() {
        Schedule schedule = new Schedule();
        assertNull(schedule.getId());
        assertNull(schedule.getTitle());
    }

    @Test
    void allArgsConstructor_ShouldSetAllFields() {
        LocalDateTime now = LocalDateTime.now();
        Schedule schedule = new Schedule(
                1L, "Task", null, now, null,
                "低", "已完成", "娱乐", null, now, now
        );

        assertEquals(1L, schedule.getId());
        assertEquals("Task", schedule.getTitle());
        assertNull(schedule.getDescription());
        assertEquals(now, schedule.getStartTime());
        assertNull(schedule.getEndTime());
        assertEquals("低", schedule.getPriority());
        assertEquals("已完成", schedule.getStatus());
        assertEquals("娱乐", schedule.getCategory());
        assertNull(schedule.getReminderTime());
        assertEquals(now, schedule.getCreatedTime());
        assertEquals(now, schedule.getUpdatedTime());
    }
}
