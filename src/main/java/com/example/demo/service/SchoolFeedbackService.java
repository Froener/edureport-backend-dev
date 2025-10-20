package com.example.demo.service;

import com.example.demo.dto.SchoolFeedbackDTO;
import com.example.demo.model.School;
import com.example.demo.model.Tag;
import com.example.demo.repository.SchoolRepository;
import com.example.demo.repository.TagRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SchoolFeedbackService {

    @Autowired
    private TagRepository tagRepository;

    @Autowired
    private SchoolRepository schoolRepository;

    public SchoolFeedbackDTO getSchoolWithFeedback(Long schoolId) {
        School school = schoolRepository.findById(schoolId).orElse(null);
        if (school == null) {
            return null;
        }

        List<Tag> positiveTags = tagRepository.findPositiveTagsBySchoolId(schoolId);
        List<Tag> negativeTags = tagRepository.findNegativeTagsBySchoolId(schoolId);

        return new SchoolFeedbackDTO(school, positiveTags, negativeTags);
    }

    public List<SchoolFeedbackDTO> getAllSchoolsWithFeedback() {
        List<School> schools = schoolRepository.findAll();

        return schools.stream().map(school -> {
            List<Tag> positiveTags = tagRepository.findPositiveTagsBySchoolId(school.getSchool_id());
            List<Tag> negativeTags = tagRepository.findNegativeTagsBySchoolId(school.getSchool_id());
            return new SchoolFeedbackDTO(school, positiveTags, negativeTags);
        }).collect(Collectors.toList());
    }
}