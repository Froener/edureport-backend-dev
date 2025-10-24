package com.example.demo.service;

import com.example.demo.dto.SchoolFeedbackDTO;
import com.example.demo.dto.TagWithCountDTO;
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

        List<TagWithCountDTO> positiveTags = convertToTagWithCountDTO(
                tagRepository.findPositiveTagsWithCountBySchoolId(schoolId)
        );

        List<TagWithCountDTO> negativeTags = convertToTagWithCountDTO(
                tagRepository.findNegativeTagsWithCountBySchoolId(schoolId)
        );

        return new SchoolFeedbackDTO(school, positiveTags, negativeTags);
    }

    public List<SchoolFeedbackDTO> getAllSchoolsWithFeedback() {
        List<School> schools = schoolRepository.findAll();

        return schools.stream().map(school -> {
            List<TagWithCountDTO> positiveTags = convertToTagWithCountDTO(
                    tagRepository.findPositiveTagsWithCountBySchoolId(school.getSchool_id())
            );

            List<TagWithCountDTO> negativeTags = convertToTagWithCountDTO(
                    tagRepository.findNegativeTagsWithCountBySchoolId(school.getSchool_id())
            );

            return new SchoolFeedbackDTO(school, positiveTags, negativeTags);
        }).collect(Collectors.toList());
    }

    private List<TagWithCountDTO> convertToTagWithCountDTO(List<Object[]> queryResults) {
        return queryResults.stream().map(row -> {
            Long tagId = (Long) row[0];
            String tagName = (String) row[1];
            Tag.TagType tagType = (Tag.TagType) row[2];
            Long count = (Long) row[3];

            return new TagWithCountDTO(tagId, tagName, tagType, count);
        }).collect(Collectors.toList());
    }
}