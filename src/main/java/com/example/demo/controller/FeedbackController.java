package com.example.demo.controller;

import com.example.demo.model.*;
import com.example.demo.repository.FeedbackRepository;
import com.example.demo.repository.SchoolRepository;
import com.example.demo.repository.StudentRepository;
import com.example.demo.repository.TagRepository;
import com.example.demo.service.AuthenticationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("api/feedback")
//@CrossOrigin(origins = "http://localhost:5173")
public class FeedbackController {
    private final FeedbackRepository repository;
    private final StudentRepository studentRepository;
    private final SchoolRepository schoolRepository;
    private final TagRepository tagRepository;
    private final AuthenticationService authService;

    public FeedbackController(FeedbackRepository repository,
                              StudentRepository studentRepository,
                              SchoolRepository schoolRepository,
                              TagRepository tagRepository,
                              AuthenticationService authService) {
        this.repository = repository;
        this.studentRepository = studentRepository;
        this.schoolRepository = schoolRepository;
        this.tagRepository = tagRepository;
        this.authService = authService;
    }

    @GetMapping
    public List<Feedback> getAll() {
        return repository.findAll();
    }

    @GetMapping("/{id}")
    public Feedback getById(@PathVariable Long id) {
        return repository.findById(id).orElseThrow();
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody Map<String, Object> payload) {
        try {
            // Get current authenticated user
            User currentUser = authService.getCurrentUserOrThrow();

            // Check if user is a student
            if (currentUser.getUser_type() != User.UserType.student) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Apenas estudantes podem enviar feedback"));
            }

            // Find student by user ID
            Student student = studentRepository.findByUserId(currentUser.getUser_id())
                    .orElseThrow(() -> new RuntimeException("Estudante não encontrado"));

            // Extract schoolId and tagIds from payload
            Long schoolId = Long.valueOf(payload.get("schoolId").toString());
            List<Integer> tagIds = (List<Integer>) payload.get("tagIds");

            // Convert List<Integer> to List<Long>
            List<Long> longTagIds = tagIds.stream()
                    .map(Integer::longValue)
                    .collect(Collectors.toList());

            // Check if feedback already exists for this student and school
            Optional<Feedback> existingFeedbackOpt = repository.findByStudentIdAndSchoolId(student.getStudent_id(), schoolId);

            Feedback feedback;
            if (existingFeedbackOpt.isPresent()) {
                // Update EXISTING feedback
                feedback = existingFeedbackOpt.get();
                feedback.setCreated_at(java.time.LocalDateTime.now());
            } else {
                // Create NEW feedback
                feedback = new Feedback();
                feedback.setStudent(student);
                feedback.setSchool(schoolRepository.findById(schoolId)
                        .orElseThrow(() -> new RuntimeException("Escola não encontrada")));
            }




            // Set tags
            Set<Tag> tags = new HashSet<>();
            for (Long tagId : longTagIds) {
                Tag tag = tagRepository.findById(tagId)
                        .orElseThrow(() -> new RuntimeException("Tag não encontrada: " + tagId));
                tags.add(tag);
            }
            feedback.setTags(tags);

            Feedback savedFeedback = repository.save(feedback);
            return ResponseEntity.ok(savedFeedback);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Falha ao criar feedback: " + e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public Feedback update(@PathVariable Long id, @RequestBody Feedback updated) {
        Feedback feedback = repository.findById(id).orElseThrow();
        feedback.setStudent(studentRepository.findById(updated.getStudent().getStudent_id()).orElseThrow());
        feedback.setSchool(schoolRepository.findById(updated.getSchool().getSchool_id()).orElseThrow());
        feedback.setTags(updated.getTags());
        return repository.save(feedback);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        repository.deleteById(id);
    }

    // Additional endpoint to get feedback by current student
    @GetMapping("/my-feedback")
    public ResponseEntity<?> getMyFeedback() {
        try {
            User currentUser = authService.getCurrentUserOrThrow();

            if (currentUser.getUser_type() != User.UserType.student) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Apenas estudantes podem visualizar feedback"));
            }

            Student student = studentRepository.findByUserId(currentUser.getUser_id())
                    .orElseThrow(() -> new RuntimeException("Estudante não encontrado"));

            List<Feedback> feedbacks = repository.findByStudent(student);
            return ResponseEntity.ok(feedbacks);

        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Falha ao buscar feedback: " + e.getMessage()));
        }
    }

    // Endpoint to get feedback by school (for school users)
    @GetMapping("/school/{schoolId}")
    public ResponseEntity<?> getFeedbackBySchool(@PathVariable Long schoolId) {
        try {
            User currentUser = authService.getCurrentUserOrThrow();

            // Verify the user has access to this school's feedback
            if (currentUser.getUser_type() == User.UserType.school) {
                // For school users, they can only see feedback for their own school
                if (!currentUser.getUser_id().equals(schoolRepository.findById(schoolId)
                        .orElseThrow(() -> new RuntimeException("Escola não encontrada"))
                        .getUser().getUser_id())) {
                    return ResponseEntity.badRequest()
                            .body(Map.of("error", "Acesso negado a feedback de outra escola"));
                }
            }

            List<Feedback> feedbacks = repository.findBySchoolSchoolId(schoolId);
            return ResponseEntity.ok(feedbacks);

        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Falha ao buscar feedback da escola: " + e.getMessage()));
        }
    }

    @GetMapping("/my-feedback/current")
    public ResponseEntity<?> getMyCurrentFeedback() {
        try {
            User currentUser = authService.getCurrentUserOrThrow();

            if (currentUser.getUser_type() != User.UserType.student) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Apenas estudantes podem visualizar feedback"));
            }

            Student student = studentRepository.findByUserId(currentUser.getUser_id())
                    .orElseThrow(() -> new RuntimeException("Estudante não encontrado"));

            // Get the student's school
            School school = student.getSchool();
            if (school == null) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Estudante não está associado a uma escola"));
            }

            // Find feedback for this student and school
            Optional<Feedback> feedbackOpt = repository.findByStudentIdAndSchoolId(student.getStudent_id(), school.getSchool_id());

            if (feedbackOpt.isPresent()) {
                return ResponseEntity.ok(feedbackOpt.get());
            } else {
                return ResponseEntity.ok(null); // No feedback submitted yet
            }

        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Falha ao buscar feedback atual: " + e.getMessage()));
        }
    }

}