package com.studentassistant.service;


import com.studentassistant.entity.Study;
import com.studentassistant.dto.StudyDTO;
import com.studentassistant.repository.StudyRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class StudyServiceTest {

    @Mock
    private StudyRepository studyRepository;

    @InjectMocks
    private StudyService studyService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateStudy() {
        StudyDTO studyDTO = new StudyDTO();
        studyDTO.setSubject("数学");
        studyDTO.setContent("复习代数");
        studyDTO.setStudyDuration(60);
        studyDTO.setStudyType("复习");
        studyDTO.setDifficultyLevel(3);

        Study study = new Study();
        study.setId(1L);
        study.setSubject("数学");
        study.setContent("复习代数");
        study.setStudyDuration(60);
        study.setStudyType("复习");
        study.setDifficultyLevel(3);

        when(studyRepository.save(any(Study.class))).thenReturn(study);

        Study result = studyService.createStudy(studyDTO);

        assertNotNull(result);
        assertEquals("数学", result.getSubject());
        assertEquals(60, result.getStudyDuration());
        verify(studyRepository, times(1)).save(any(Study.class));
    }

    @Test
    void testGetAllStudies() {
        Study study1 = new Study();
        study1.setId(1L);
        study1.setSubject("数学");

        Study study2 = new Study();
        study2.setId(2L);
        study2.setSubject("英语");

        when(studyRepository.findAll()).thenReturn(Arrays.asList(study1, study2));

        var result = studyService.getAllStudies();

        assertEquals(2, result.size());
        verify(studyRepository, times(1)).findAll();
    }

    @Test
    void testGetStudyById() {
        Study study = new Study();
        study.setId(1L);
        study.setSubject("数学");

        when(studyRepository.findById(1L)).thenReturn(Optional.of(study));

        var result = studyService.getStudyById(1L);

        assertTrue(result.isPresent());
        assertEquals("数学", result.get().getSubject());
        verify(studyRepository, times(1)).findById(1L);
    }

    @Test
    void testUpdateStudy() {
        StudyDTO studyDTO = new StudyDTO();
        studyDTO.setSubject("英语");
        studyDTO.setContent("学习语法");
        studyDTO.setStudyDuration(45);
        studyDTO.setStudyType("课程");
        studyDTO.setDifficultyLevel(2);

        Study existingStudy = new Study();
        existingStudy.setId(1L);
        existingStudy.setSubject("数学");

        when(studyRepository.findById(1L)).thenReturn(Optional.of(existingStudy));
        when(studyRepository.save(any(Study.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Study result = studyService.updateStudy(1L, studyDTO);

        assertNotNull(result);
        assertEquals("英语", result.getSubject());
        assertEquals(45, result.getStudyDuration());
        verify(studyRepository, times(1)).findById(1L);
        verify(studyRepository, times(1)).save(any(Study.class));
    }

    @Test
    void testDeleteStudy() {
        doNothing().when(studyRepository).deleteById(1L);

        studyService.deleteStudy(1L);

        verify(studyRepository, times(1)).deleteById(1L);
    }

    @Test
    void testUpdateStudyThrowsExceptionWhenStudyNotFound() {
        StudyDTO studyDTO = new StudyDTO();
        studyDTO.setSubject("英语");
        studyDTO.setContent("学习语法");
        studyDTO.setStudyDuration(45);
        studyDTO.setStudyType("课程");
        studyDTO.setDifficultyLevel(2);

        when(studyRepository.findById(1L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            studyService.updateStudy(1L, studyDTO);
        });

        assertEquals("学习记录不存在", exception.getMessage());
        verify(studyRepository, times(1)).findById(1L);
    }

    @Test
    void testGetStudyByIdReturnsEmptyOptionalWhenStudyNotFound() {
        when(studyRepository.findById(1L)).thenReturn(Optional.empty());

        Optional<Study> result = studyService.getStudyById(1L);

        assertTrue(result.isEmpty());
        verify(studyRepository, times(1)).findById(1L);
    }

    @Test
    void testDeleteStudyDoesNotThrowExceptionWhenStudyNotFound() {
        doThrow(new RuntimeException("学习记录不存在")).when(studyRepository).deleteById(1L);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            studyService.deleteStudy(1L);
        });

        assertEquals("学习记录不存在", exception.getMessage());
        verify(studyRepository, times(1)).deleteById(1L);
    }
}
