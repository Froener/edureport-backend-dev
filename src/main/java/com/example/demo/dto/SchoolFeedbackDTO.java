package com.example.demo.dto;

import com.example.demo.model.School;
import com.example.demo.model.Tag;
import java.util.List;

public class SchoolFeedbackDTO {
    private School school;
    private List<Tag> positiveTags;
    private List<Tag> negativeTags;

    public SchoolFeedbackDTO(School school, List<Tag> positiveTags, List<Tag> negativeTags) {
        this.school = school;
        this.positiveTags = positiveTags;
        this.negativeTags = negativeTags;
    }

    // Getters and setters
    public School getSchool() { return school; }
    public void setSchool(School school) { this.school = school; }

    public List<Tag> getPositiveTags() { return positiveTags; }
    public void setPositiveTags(List<Tag> positiveTags) { this.positiveTags = positiveTags; }

    public List<Tag> getNegativeTags() { return negativeTags; }
    public void setNegativeTags(List<Tag> negativeTags) { this.negativeTags = negativeTags; }
}