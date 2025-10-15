package com.example.demo.controller;


import com.example.demo.model.School;
import com.example.demo.model.User;
import com.example.demo.repository.SchoolRepository;
import com.example.demo.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/schools")
@CrossOrigin(origins = "http://localhost:5173")
public class SchoolController {

    private final SchoolRepository repository;
    private final UserRepository userRepository;

    public SchoolController(SchoolRepository repository, UserRepository userRepository) {
        this.repository = repository;
        this.userRepository = userRepository;
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
