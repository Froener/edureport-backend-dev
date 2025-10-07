package com.example.demo.controller;


import com.example.demo.model.Admin;
import com.example.demo.repository.AdminRepository;
import com.example.demo.repository.UserRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admins")
@CrossOrigin(origins = "http://localhost:5173")
public class AdminController {

    private final AdminRepository repository;
    private final UserRepository userRepository;

    public AdminController(AdminRepository repository, UserRepository userRepository) {
        this.repository = repository;
        this.userRepository = userRepository;
    }

    @GetMapping
    public List<Admin> getAll() {
        return repository.findAll();
    }

    @GetMapping("/{id}")
    public Admin getById(@PathVariable Long id) {
        return repository.findById(id).orElseThrow();
    }

    @PostMapping
    public Admin create(@RequestBody Admin admin) {
        admin.setUser(userRepository.findById(admin.getUser().getUser_id()).orElseThrow());
        return repository.save(admin);
    }

    @PutMapping("/{id}")
    public Admin update(@PathVariable Long id, @RequestBody Admin updated) {
        Admin admin = repository.findById(id).orElseThrow();
        admin.setUser(userRepository.findById(updated.getUser().getUser_id()).orElseThrow());
        return repository.save(admin);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        repository.deleteById(id);
    }
}
