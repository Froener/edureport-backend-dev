package com.example.demo.controller;

import com.example.demo.dto.SchoolFeedbackDTO;
import com.example.demo.model.School;
import com.example.demo.model.Student;
import com.example.demo.model.User;
import com.example.demo.repository.SchoolRepository;
import com.example.demo.repository.StudentRepository;
import com.example.demo.service.AuthenticationService;
import com.example.demo.service.SchoolFeedbackService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/school-feedback")
public class SchoolFeedbackController {

    private final SchoolFeedbackService schoolFeedbackService;
    private final AuthenticationService authService;
    private final SchoolRepository schoolRepository;
    private final StudentRepository studentRepository;

    public SchoolFeedbackController(
            SchoolFeedbackService schoolFeedbackService,
            AuthenticationService authService,
            SchoolRepository schoolRepository,
            StudentRepository studentRepository) {
        this.schoolFeedbackService = schoolFeedbackService;
        this.authService = authService;
        this.schoolRepository = schoolRepository;
        this.studentRepository = studentRepository;
    }

    // ADMIN ONLY - Get all schools with feedback
    @GetMapping("/all")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<?> getAllSchoolsWithFeedback() {
        try {
            List<SchoolFeedbackDTO> schoolsWithFeedback = schoolFeedbackService.getAllSchoolsWithFeedback();
            return ResponseEntity.ok(schoolsWithFeedback);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Erro ao buscar escolas com feedback: " + e.getMessage()));
        }
    }

    // SCHOOL ONLY - Get feedback for their own school
    @GetMapping("/my-school")
    @PreAuthorize("hasAuthority('ROLE_SCHOOL')")
    public ResponseEntity<?> getMySchoolFeedback() {
        try {
            User currentUser = authService.getCurrentUserOrThrow();

            // Find the school associated with this user
            School school = schoolRepository.findByUserId(currentUser.getUser_id())
                    .orElseThrow(() -> new RuntimeException("Escola não encontrada para este usuário"));

            SchoolFeedbackDTO schoolFeedback = schoolFeedbackService.getSchoolWithFeedback(school.getSchool_id());

            if (schoolFeedback == null) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Feedback não encontrado"));
            }

            return ResponseEntity.ok(schoolFeedback);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Erro ao buscar feedback da escola: " + e.getMessage()));
        }
    }

    // STUDENT ONLY - Get feedback for their school
    @GetMapping("/student-school")
    @PreAuthorize("hasAuthority('ROLE_STUDENT')")
    public ResponseEntity<?> getStudentSchoolFeedback() {
        try {
            User currentUser = authService.getCurrentUserOrThrow();

            // Find the student and their school
            Student student = studentRepository.findByUserId(currentUser.getUser_id())
                    .orElseThrow(() -> new RuntimeException("Estudante não encontrado"));

            School school = student.getSchool();
            if (school == null) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Escola não encontrada para este estudante"));
            }

            SchoolFeedbackDTO schoolFeedback = schoolFeedbackService.getSchoolWithFeedback(school.getSchool_id());

            if (schoolFeedback == null) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Feedback não encontrado"));
            }

            return ResponseEntity.ok(schoolFeedback);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Erro ao buscar feedback da escola: " + e.getMessage()));
        }
    }
}