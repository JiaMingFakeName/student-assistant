package com.studentassistant.dto;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class ScheduleDTOTest {

    private static Validator validator;

    @BeforeAll
    static void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void validScheduleDTO_ShouldPassValidation() {
        ScheduleDTO dto = new ScheduleDTO();
        dto.setTitle("Meeting");
        dto.setStartTime(LocalDateTime.now());
        dto.setPriority("高");
        dto.setStatus("待办");
        dto.setCategory("学习");

        assertTrue(validator.validate(dto).isEmpty());
    }

    @Test
    void nullTitle_ShouldFailValidation() {
        ScheduleDTO dto = createValidDTO();
        dto.setTitle(null);
        assertEquals(1, validator.validate(dto).size());
    }

    @Test
    void blankTitle_ShouldFailValidation() {
        ScheduleDTO dto = createValidDTO();
        dto.setTitle("");
        assertEquals(1, validator.validate(dto).size());
    }

    @Test
    void nullStartTime_ShouldFailValidation() {
        ScheduleDTO dto = createValidDTO();
        dto.setStartTime(null);
        assertEquals(1, validator.validate(dto).size());
    }

    @Test
    void nullPriority_ShouldFailValidation() {
        ScheduleDTO dto = createValidDTO();
        dto.setPriority(null);
        assertEquals(1, validator.validate(dto).size());
    }

    @Test
    void nullStatus_ShouldFailValidation() {
        ScheduleDTO dto = createValidDTO();
        dto.setStatus(null);
        assertEquals(1, validator.validate(dto).size());
    }

    @Test
    void nullCategory_ShouldFailValidation() {
        ScheduleDTO dto = createValidDTO();
        dto.setCategory(null);
        assertEquals(1, validator.validate(dto).size());
    }

    @Test
    void endTimeAfterStartTime_ShouldBeValid() {
        ScheduleDTO dto = createValidDTO();
        dto.setEndTime(LocalDateTime.now().plusHours(1));
        assertTrue(validator.validate(dto).isEmpty());
    }

    // Helper method
    private ScheduleDTO createValidDTO() {
        ScheduleDTO dto = new ScheduleDTO();
        dto.setTitle("Valid Title");
        dto.setStartTime(LocalDateTime.now());
        dto.setPriority("中");
        dto.setStatus("进行中");
        dto.setCategory("生活");
        return dto;
    }
}