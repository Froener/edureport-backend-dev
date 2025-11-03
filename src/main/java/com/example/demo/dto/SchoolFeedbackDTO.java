package com.example.demo.dto;

import com.example.demo.model.School;
import java.util.List;

public class SchoolFeedbackDTO {
    private School school;
    private List<TagWithCountDTO> positiveTags;
    private List<TagWithCountDTO> negativeTags;
    private Long uniqueStudentCount;
    private Long totalStudentCount;

    public SchoolFeedbackDTO(School school, List<TagWithCountDTO> positiveTags, List<TagWithCountDTO> negativeTags, Long uniqueStudentCount, Long totalStudentCount) {
        this.school = school;
        this.positiveTags = positiveTags;
        this.negativeTags = negativeTags;
        this.uniqueStudentCount = uniqueStudentCount;
        this.totalStudentCount = totalStudentCount;
    }

    // Getters and setters
    public School getSchool() { return school; }
    public void setSchool(School school) { this.school = school; }

    public List<TagWithCountDTO> getPositiveTags() { return positiveTags; }
    public void setPositiveTags(List<TagWithCountDTO> positiveTags) { this.positiveTags = positiveTags; }

    public List<TagWithCountDTO> getNegativeTags() { return negativeTags; }
    public void setNegativeTags(List<TagWithCountDTO> negativeTags) { this.negativeTags = negativeTags; }

    public Long getUniqueStudentCount() { return uniqueStudentCount; }
    public void setUniqueStudentCount(Long uniqueStudentCount) { this.uniqueStudentCount = uniqueStudentCount; }

    public Long getTotalStudentCount() { return totalStudentCount; }
    public void setTotalStudentCount(Long totalStudentCount) { this.totalStudentCount = totalStudentCount; }
}