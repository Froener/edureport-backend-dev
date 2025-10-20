package com.example.demo.repository;

import com.example.demo.model.Feedback;
import com.example.demo.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface FeedbackRepository extends JpaRepository<Feedback, Long> {

    // Find feedback by student
    List<Feedback> findByStudent(Student student);

    // Find feedback by school ID
    @Query("SELECT f FROM Feedback f WHERE f.school.school_id = :schoolId")
    List<Feedback> findBySchoolSchoolId(@Param("schoolId") Long schoolId);

    // Find feedback by student ID
    @Query("SELECT f FROM Feedback f WHERE f.student.student_id = :studentId")
    List<Feedback> findByStudentId(@Param("studentId") Long studentId);
}