package com.studentassistant.service;

import com.studentassistant.entity.Study;
import com.studentassistant.dto.StudyDTO;
import com.studentassistant.repository.StudyRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class StudyService {

    @Autowired
    private StudyRepository studyRepository;

    public Study createStudy(StudyDTO studyDTO) {
        Study study = new Study();
        BeanUtils.copyProperties(studyDTO, study);
        return studyRepository.save(study);
    }

    public List<Study> getAllStudies() {
        return studyRepository.findAll();
    }

    public Optional<Study> getStudyById(Long id) {
        return studyRepository.findById(id);
    }

    public Study updateStudy(Long id, StudyDTO studyDTO) {
        Study study = studyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("学习记录不存在"));

        BeanUtils.copyProperties(studyDTO, study);
        study.setId(id);
        return studyRepository.save(study);
    }

    public void deleteStudy(Long id) {
        studyRepository.deleteById(id);
    }

    public List<Study> getStudiesBySubject(String subject) {
        return studyRepository.findBySubject(subject);
    }

    public List<Study> getStudiesByDateRange(LocalDateTime start, LocalDateTime end) {
        return studyRepository.findStudyRecordsByDateRange(start, end);
    }

    public Integer getTotalStudyDuration(LocalDateTime start, LocalDateTime end) {
        Integer total = studyRepository.getTotalStudyDurationByDateRange(start, end);
        return total != null ? total : 0;
    }
}