package com.example.demo.repository;

import com.example.demo.model.Tag;
import com.example.demo.model.School;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface TagRepository extends JpaRepository<Tag, Long> {

    @Query("SELECT t FROM Tag t WHERE t.school.school_id = :schoolId")
    List<Tag> findBySchoolId(Long schoolId);

    @Query("SELECT t FROM Tag t WHERE t.school.school_id = :schoolId AND t.tag_positivo_negativo = 'positive'")
    List<Tag> findPositiveTagsBySchoolId(Long schoolId);

    @Query("SELECT t FROM Tag t WHERE t.school.school_id = :schoolId AND t.tag_positivo_negativo = 'negative'")
    List<Tag> findNegativeTagsBySchoolId(Long schoolId);

    @Query("SELECT s, t FROM School s LEFT JOIN Tag t ON s.school_id = t.school.school_id")
    List<Object[]> findAllSchoolsWithTags();

    @Query("SELECT s, t FROM School s LEFT JOIN Tag t ON s.school_id = t.school.school_id WHERE s.school_id = :schoolId")
    List<Object[]> findSchoolWithTagsById(Long schoolId);
}