package com.example.demo.controller;


import com.example.demo.dto.SchoolFeedbackDTO;
import com.example.demo.model.School;
import com.example.demo.model.User;
import com.example.demo.repository.SchoolRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.SchoolFeedbackService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/schools")
//@CrossOrigin(origins = "http://localhost:5173")
public class SchoolController {

    private final SchoolRepository repository;
    private final UserRepository userRepository;
    private final SchoolFeedbackService schoolFeedbackService;

    public SchoolController(SchoolRepository repository, UserRepository userRepository, SchoolFeedbackService schoolFeedbackService) {
        this.repository = repository;
        this.userRepository = userRepository;
        this.schoolFeedbackService = schoolFeedbackService;
    }

    @GetMapping
    public List<School> getAll() {
        return repository.findAll();
    }

    @GetMapping("/{id}")
    public School getById(@PathVariable Long id) {
        return repository.findById(id).orElseThrow();
    }

    @GetMapping("/name/{name}")
    public ResponseEntity<List<School>> getSchoolByName(@PathVariable String name) {
        List<School> schools = repository.findBySchoolNameContainingIgnoreCase(name);

        if (schools.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(schools);

    }
    @GetMapping("/{schoolId}/feedback")
    public ResponseEntity<?> getSchoolWithFeedback(@PathVariable Long schoolId) {
        try {
            SchoolFeedbackDTO schoolFeedback = schoolFeedbackService.getSchoolWithFeedback(schoolId);
            if (schoolFeedback == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "Escola não encontrada"));
            }
            return ResponseEntity.ok(schoolFeedback);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Erro ao buscar feedback da escola"));
        }
    }

    @GetMapping("/feedback")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<?> getAllSchoolsWithFeedback() {
        // This is now deprecated - use /api/school-feedback/all instead
        try {
            List<SchoolFeedbackDTO> schoolsWithFeedback = schoolFeedbackService.getAllSchoolsWithFeedback();
            return ResponseEntity.ok(schoolsWithFeedback);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Erro ao buscar escolas com feedback"));
        }
    }

    @PutMapping("/{id}")
    public School update(@PathVariable Long id, @RequestBody School updated) {
        School school = repository.findById(id).orElseThrow();
        school.setSchoolName(updated.getSchoolName());
        school.setSchool_type(updated.getSchool_type());
        school.setUser(userRepository.findById(updated.getUser().getUser_id()).orElseThrow());
        return repository.save(school);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        repository.deleteById(id);
    }
}
