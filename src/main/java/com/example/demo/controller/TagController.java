package com.example.demo.controller;

import com.example.demo.model.School;
import com.example.demo.model.Tag;
import com.example.demo.repository.SchoolRepository;
import com.example.demo.repository.TagRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/tags")
//@CrossOrigin(origins = "http://localhost:5173")
public class TagController {

    private final TagRepository tagRepository;
    private final SchoolRepository schoolRepository;

    public TagController(TagRepository tagRepository, SchoolRepository schoolRepository) {
        this.tagRepository = tagRepository;
        this.schoolRepository = schoolRepository;
    }

    // Existing endpoint - keep as is
    @GetMapping("/school/{schoolId}")
    public ResponseEntity<?> getTagsBySchool(@PathVariable Long schoolId) {
        Optional<School> schoolOpt = schoolRepository.findById(schoolId);
        if (schoolOpt.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Escola não encontrada"));
        }
        List<Tag> tags = tagRepository.findBySchoolId(schoolId);
        return ResponseEntity.ok(tags);
    }

    // NEW ENDPOINT - Get only positive tags for a school
    @GetMapping("/school/{schoolId}/positive")
    public ResponseEntity<?> getPositiveTagsBySchool(@PathVariable Long schoolId) {
        Optional<School> schoolOpt = schoolRepository.findById(schoolId);
        if (schoolOpt.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Escola não encontrada"));
        }
        List<Tag> positiveTags = tagRepository.findPositiveTagsBySchoolId(schoolId);
        return ResponseEntity.ok(positiveTags);
    }

    // NEW ENDPOINT - Get only negative tags for a school
    @GetMapping("/school/{schoolId}/negative")
    public ResponseEntity<?> getNegativeTagsBySchool(@PathVariable Long schoolId) {
        Optional<School> schoolOpt = schoolRepository.findById(schoolId);
        if (schoolOpt.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Escola não encontrada"));
        }
        List<Tag> negativeTags = tagRepository.findNegativeTagsBySchoolId(schoolId);
        return ResponseEntity.ok(negativeTags);
    }

    // Updated to handle both old and new payload structures
    @PostMapping("/school/{schoolId}")
    public ResponseEntity<?> createTag(
            @PathVariable Long schoolId,
            @RequestBody Map<String, String> payload) {

        Optional<School> schoolOpt = schoolRepository.findById(schoolId);
        if (schoolOpt.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Escola não encontrada"));
        }

        // Support both old and new field names
        String label = payload.get("label") != null ? payload.get("label") : payload.get("tag_nome");
        String type = payload.get("type") != null ? payload.get("type") : payload.get("tag_positivo_negativo");

        if (label == null || label.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Label/tag_nome é obrigatório"));
        }

        if (type == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "Type/tag_positivo_negativo é obrigatório"));
        }

        Tag tag = new Tag();
        tag.setTag_nome(label.trim());

        // Handle both old ("GOOD"/"BAD") and new ("positive"/"negative") type values
        try {
            if (type.equalsIgnoreCase("GOOD") || type.equalsIgnoreCase("positive")) {
                tag.setTag_positivo_negativo(Tag.TagType.positive);
            } else if (type.equalsIgnoreCase("BAD") || type.equalsIgnoreCase("negative")) {
                tag.setTag_positivo_negativo(Tag.TagType.negative);
            } else {
                // Try to parse directly as enum
                tag.setTag_positivo_negativo(Tag.TagType.valueOf(type.toLowerCase()));
            }
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Tipo inválido. Use 'positive' ou 'negative'"));
        }

        tag.setSchool(schoolOpt.get());

        return ResponseEntity.ok(tagRepository.save(tag));
    }

    // Existing endpoint - keep as is
    @DeleteMapping("/{tagId}")
    public ResponseEntity<?> deleteTag(@PathVariable Long tagId) {
        if (!tagRepository.existsById(tagId)) {
            return ResponseEntity.badRequest().body(Map.of("error", "Tag não encontrada"));
        }
        tagRepository.deleteById(tagId);
        return ResponseEntity.ok(Map.of("message", "Tag removida com sucesso"));
    }
}