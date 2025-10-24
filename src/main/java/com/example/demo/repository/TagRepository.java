package com.example.demo.repository;

import com.example.demo.model.Tag;
import com.example.demo.model.School;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

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

    // Get tags with usage count for positive tags
    @Query("SELECT t.tag_id, t.tag_nome, t.tag_positivo_negativo, COUNT(f.feedback_id) " +
            "FROM Tag t " +
            "LEFT JOIN t.school s " +
            "LEFT JOIN Feedback f ON t MEMBER OF f.tags " +
            "WHERE s.school_id = :schoolId AND t.tag_positivo_negativo = 'positive' " +
            "GROUP BY t.tag_id, t.tag_nome, t.tag_positivo_negativo")
    List<Object[]> findPositiveTagsWithCountBySchoolId(@Param("schoolId") Long schoolId);

    //Get tags with usage count for negative tags
    @Query("SELECT t.tag_id, t.tag_nome, t.tag_positivo_negativo, COUNT(f.feedback_id) " +
            "FROM Tag t " +
            "LEFT JOIN t.school s " +
            "LEFT JOIN Feedback f ON t MEMBER OF f.tags " +
            "WHERE s.school_id = :schoolId AND t.tag_positivo_negativo = 'negative' " +
            "GROUP BY t.tag_id, t.tag_nome, t.tag_positivo_negativo")
    List<Object[]> findNegativeTagsWithCountBySchoolId(@Param("schoolId") Long schoolId);

}