package com.example.demo.controller;

import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/users")
//@CrossOrigin(origins = "http://localhost:5173")
public class UserController {
    private final UserRepository repository;

    public UserController(UserRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public List<User> getAll() {
        return repository.findAll();
    }

    @GetMapping("/{id}")
    public User getByID(@PathVariable Long id) {
        return repository.findById(id).orElseThrow();
    }

    @PostMapping
    public User create(@RequestBody User user) {
        return repository.save(user);
    }

    @PutMapping("/{id}")
    public User update(@PathVariable Long id, @RequestBody User updated) {
        User user = repository.findById(id).orElseThrow();
        user.setFull_name(updated.getFull_name());
        user.setSocial_name(updated.getSocial_name());
        user.setEmail(updated.getEmail());
        user.setPassword_hash(updated.getPassword_hash());
        user.setBirth_date(updated.getBirth_date());
        user.setAddress_state(updated.getAddress_state());
        user.setAddress_city(updated.getAddress_city());
        user.setAddress_neighborhood(updated.getAddress_neighborhood());
        user.setUser_type(updated.getUser_type());
        return repository.save(user);
    }
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        repository.deleteById(id);
    }
}
