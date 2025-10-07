package com.example.demo.controller;

import com.example.demo.model.Tag;
import com.example.demo.repository.TagRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/tags")
@CrossOrigin(origins = "http://localhost:5173")
public class TagController {
    private final TagRepository repository;

    public TagController(TagRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public List<Tag> getAll() {
        return repository.findAll();
    }

    @GetMapping("/{id}")
    public Tag getById(@PathVariable Long id) {
        return repository.findById(id).orElseThrow();
    }

    @PostMapping
    public Tag create(@RequestBody Tag tag) {
        return repository.save(tag);
    }

    @PutMapping("/{id}")
    public Tag update(@PathVariable Long id, @RequestBody Tag updated) {
        Tag tag = repository.findById(id).orElseThrow();
        tag.setTag_nome(updated.getTag_nome());
        tag.setTag_positivo_negativo(updated.getTag_positivo_negativo());
        return repository.save(tag);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        repository.deleteById(id);
    }
}
