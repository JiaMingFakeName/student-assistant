package com.studentassistant.controller;

import com.studentassistant.entity.Study;
import com.studentassistant.dto.StudyDTO;
import com.studentassistant.service.StudyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/study")
public class StudyController {

    @Autowired
    private StudyService studyService;

    @PostMapping
    public ResponseEntity<Study> createStudy(@Valid @RequestBody StudyDTO studyDTO) {
        Study study = studyService.createStudy(studyDTO);
        return ResponseEntity.ok(study);
    }

    @GetMapping
    public ResponseEntity<List<Study>> getAllStudies() {
        List<Study> studies = studyService.getAllStudies();
        return ResponseEntity.ok(studies);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Study> getStudyById(@PathVariable Long id) {
        return studyService.getStudyById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Study> updateStudy(@PathVariable Long id, @Valid @RequestBody StudyDTO studyDTO) {
        Study study = studyService.updateStudy(id, studyDTO);
        return ResponseEntity.ok(study);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStudy(@PathVariable Long id) {
        studyService.deleteStudy(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/subject/{subject}")
    public ResponseEntity<List<Study>> getStudiesBySubject(@PathVariable String subject) {
        List<Study> studies = studyService.getStudiesBySubject(subject);
        return ResponseEntity.ok(studies);
    }

    @GetMapping("/statistics")
    public ResponseEntity<Map<String, Object>> getStudyStatistics(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {

        List<Study> studies = studyService.getStudiesByDateRange(start, end);
        Integer totalDuration = studyService.getTotalStudyDuration(start, end);

        Map<String, Object> statistics = new HashMap<>();
        statistics.put("totalRecords", studies.size());
        statistics.put("totalDuration", totalDuration);
        statistics.put("averageDuration", studies.isEmpty() ? 0 : totalDuration / studies.size());
        statistics.put("records", studies);

        return ResponseEntity.ok(statistics);
    }
}