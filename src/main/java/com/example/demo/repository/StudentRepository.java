package com.example.demo.repository;

import com.example.demo.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.Optional;

public interface StudentRepository extends JpaRepository<Student, Long> {
    @Query("SELECT s FROM Student s WHERE s.user.user_id = :userId")
    Optional<Student> findByUserId(@Param("userId") Long userId);

    @Query("SELECT COUNT(s) FROM Student s WHERE s.school.school_id = :schoolId")
    Long countBySchoolId(@Param("schoolId") Long schoolId);
}