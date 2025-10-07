package com.example.demo.controller;

import com.example.demo.model.Feedback;
import com.example.demo.model.Tag;
import com.example.demo.repository.FeedbackRepository;
import com.example.demo.repository.SchoolRepository;
import com.example.demo.repository.StudentRepository;
import com.example.demo.repository.TagRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("api/feedback")
@CrossOrigin(origins = "http://localhost:5173")
public class FeedbackController {
    private final FeedbackRepository repository;
    private final StudentRepository studentRepository;
    private final SchoolRepository schoolRepository;
    private final TagRepository tagRepository;

    public FeedbackController(FeedbackRepository repository, StudentRepository studentRepository, SchoolRepository schoolRepository, TagRepository tagRepository) {
        this.repository = repository;
        this.studentRepository = studentRepository;
        this.schoolRepository = schoolRepository;
        this.tagRepository = tagRepository;
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
    public Feedback create(@RequestBody Feedback feedback) {
        feedback.setStudent(studentRepository.findById(feedback.getStudent().getStudent_id()).orElseThrow());
        feedback.setSchool(schoolRepository.findById(feedback.getSchool().getSchool_id()).orElseThrow());

        if(feedback.getTags() != null) {
            Set<Tag> tags = feedback.getTags();
            for(Tag tag : tags) {
                Tag dbTag = tagRepository.findById(tag.getTag_id()).orElseThrow();
                tag.setTag_nome(dbTag.getTag_nome());
                tag.setTag_positivo_negativo(dbTag.getTag_positivo_negativo());
            }
        }
        return repository.save(feedback);
    }

    @PutMapping("/{id}")
    public Feedback update (@PathVariable Long id, @RequestBody Feedback updated) {
        Feedback feedback = repository.findById(id).orElseThrow();
        feedback.setStudent(studentRepository.findById(updated.getStudent().getStudent_id()).orElseThrow());
        feedback.setSchool(schoolRepository.findById(updated.getSchool().getSchool_id()).orElseThrow());
        feedback.setTags(updated.getTags());
        return repository.save(feedback);
    }
    @DeleteMapping
    public void delete(@PathVariable Long id) {
        repository.deleteById(id);
    }
}
