package com.example.demo.controller;

import com.example.demo.model.Student;
import com.example.demo.repository.SchoolRepository;
import com.example.demo.repository.StudentRepository;
import com.example.demo.repository.UserRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/students")
//@CrossOrigin(origins = "http://localhost:5173")
public class StudentController {

    private final StudentRepository repository;
    private final UserRepository userRepository;
    private final SchoolRepository schoolRepository;

    public StudentController(StudentRepository repository, UserRepository userRepository, SchoolRepository schoolRepository) {
        this.repository = repository;
        this.userRepository = userRepository;
        this.schoolRepository = schoolRepository;
    }

    @GetMapping
    public List<Student> getAll() {
        return repository.findAll();
    }

    @GetMapping("/{id}")
    public Student getById(@PathVariable Long id) {
        return repository.findById(id).orElseThrow();
    }

    @PostMapping
    public Student create(@RequestBody Student student) {
        student.setUser(userRepository.findById(student.getUser().getUser_id()).orElseThrow());
        student.setSchool(schoolRepository.findById(student.getSchool().getSchool_id()).orElseThrow());
        return repository.save(student);
    }

    @PutMapping("/{id}")
    public Student update(@PathVariable Long id, @RequestBody Student updated) {
        Student student = repository.findById(id).orElseThrow();
        student.setUser(userRepository.findById(updated.getUser().getUser_id()).orElseThrow());
        student.setSchool(schoolRepository.findById(updated.getSchool().getSchool_id()).orElseThrow());
        return repository.save(student);
    }
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        repository.deleteById(id);
    }
}
