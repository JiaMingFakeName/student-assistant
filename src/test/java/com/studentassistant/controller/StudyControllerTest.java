package com.studentassistant.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.studentassistant.dto.StudyDTO;
import com.studentassistant.entity.Study;
import com.studentassistant.service.StudyService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Optional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.mockito.Mockito.*;

@WebMvcTest(StudyController.class)
class StudyControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private StudyService studyService;

    @Autowired
    private ObjectMapper objectMapper;

    private Study study;
    private StudyDTO studyDTO;

    @BeforeEach
    void setUp() {
        study = new Study(1L, "数学", "复习代数", 60, "复习", 3, null, LocalDateTime.now(), LocalDateTime.now());
        studyDTO = new StudyDTO();
        studyDTO.setSubject("数学");
        studyDTO.setContent("复习代数");
        studyDTO.setStudyDuration(60);
        studyDTO.setStudyType("复习");
        studyDTO.setDifficultyLevel(3);
    }

    @Test
    void testCreateStudy() throws Exception {
        when(studyService.createStudy(any(StudyDTO.class))).thenReturn(study);

        mockMvc.perform(post("/study")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(studyDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.subject").value("数学"))
                .andExpect(jsonPath("$.studyDuration").value(60));

        verify(studyService, times(1)).createStudy(any(StudyDTO.class));
    }

    @Test
    void testGetAllStudies() throws Exception {
        when(studyService.getAllStudies()).thenReturn(Arrays.asList(study));

        mockMvc.perform(get("/study"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].subject").value("数学"));

        verify(studyService, times(1)).getAllStudies();
    }

    @Test
    void testGetStudyById() throws Exception {
        when(studyService.getStudyById(1L)).thenReturn(Optional.of(study));

        mockMvc.perform(get("/study/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.subject").value("数学"));

        verify(studyService, times(1)).getStudyById(1L);
    }

    @Test
    void testUpdateStudy() throws Exception {
        when(studyService.updateStudy(eq(1L), any(StudyDTO.class))).thenReturn(study);

        mockMvc.perform(put("/study/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(studyDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.subject").value("数学"));

        verify(studyService, times(1)).updateStudy(eq(1L), any(StudyDTO.class));
    }

    @Test
    void testDeleteStudy() throws Exception {
        doNothing().when(studyService).deleteStudy(1L);

        mockMvc.perform(delete("/study/1"))
                .andExpect(status().isOk());

        verify(studyService, times(1)).deleteStudy(1L);
    }

    @Test
    void testGetStudiesBySubject() throws Exception {
        when(studyService.getStudiesBySubject("数学")).thenReturn(Arrays.asList(study));

        mockMvc.perform(get("/study/subject/数学"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].subject").value("数学"));

        verify(studyService, times(1)).getStudiesBySubject("数学");
    }

    @Test
    void testGetStudyStatistics() throws Exception {
        when(studyService.getStudiesByDateRange(any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(Arrays.asList(study));
        when(studyService.getTotalStudyDuration(any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(60);

        mockMvc.perform(get("/study/statistics")
                        .param("start", "2023-01-01T00:00:00")
                        .param("end", "2023-12-31T23:59:59"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalRecords").value(1))
                .andExpect(jsonPath("$.totalDuration").value(60));

        verify(studyService, times(1)).getStudiesByDateRange(any(LocalDateTime.class), any(LocalDateTime.class));
        verify(studyService, times(1)).getTotalStudyDuration(any(LocalDateTime.class), any(LocalDateTime.class));
    }
}