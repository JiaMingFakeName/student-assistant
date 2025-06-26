package com.studentassistant.dto;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class HealthDTOTest {

    @Test
    void testValidDTOFields() {
        HealthDTO dto = new HealthDTO();
        dto.setMoodScore(8);
        dto.setSleepHours(7.0);
        assertEquals(8, dto.getMoodScore());
        assertEquals(7.0, dto.getSleepHours());
    }

    @Test
    void testNullMoodScore() {
        HealthDTO dto = new HealthDTO();
        assertThrows(NullPointerException.class, () -> {
            if (dto.getMoodScore() == null) throw new NullPointerException();
        });
    }
}
