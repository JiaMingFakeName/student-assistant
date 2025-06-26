package com.studentassistant.dto;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import jakarta.validation.*;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class StudyDTOTest {

    private static Validator validator;

    @BeforeAll
    static void setUpValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void testValidStudyDTO() {
        StudyDTO dto = new StudyDTO();
        dto.setSubject("数学");
        dto.setContent("内容");
        dto.setStudyDuration(30);
        dto.setStudyType("课程");
        dto.setDifficultyLevel(3);

        Set<ConstraintViolation<StudyDTO>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty());
    }

    @Test
    void testInvalidStudyDTO() {
        StudyDTO dto = new StudyDTO();
        dto.setSubject(""); // 为空
        dto.setContent(null); // 为空
        dto.setStudyDuration(0); // 不合法
        dto.setStudyType(""); // 为空
        dto.setDifficultyLevel(6); // 超范围

        Set<ConstraintViolation<StudyDTO>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("subject")));
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("content")));
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("studyDuration")));
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("studyType")));
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("difficultyLevel")));
    }
}
