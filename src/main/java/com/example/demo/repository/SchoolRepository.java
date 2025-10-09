package com.example.demo.repository;

import com.example.demo.model.School;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SchoolRepository extends JpaRepository<School, Long> {
    Optional<School> findBySchoolName(String school_name);
}
