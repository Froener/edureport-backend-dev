package com.example.demo.dto;

import com.example.demo.model.Tag;

public class TagWithCountDTO {
    private Long tagId;
    private String tagName;
    private Tag.TagType tagType;
    private Long usageCount;

    public TagWithCountDTO(Long tagId, String tagName, Tag.TagType tagType, Long usageCount) {
        this.tagId = tagId;
        this.tagName = tagName;
        this.tagType = tagType;
        this.usageCount = usageCount;
    }

    // Getters and setters
    public Long getTagId() { return tagId; }
    public void setTagId(Long tagId) { this.tagId = tagId; }

    public String getTagName() { return tagName; }
    public void setTagName(String tagName) { this.tagName = tagName; }

    public Tag.TagType getTagType() { return tagType; }
    public void setTagType(Tag.TagType tagType) { this.tagType = tagType; }

    public Long getUsageCount() { return usageCount; }
    public void setUsageCount(Long usageCount) { this.usageCount = usageCount; }
}