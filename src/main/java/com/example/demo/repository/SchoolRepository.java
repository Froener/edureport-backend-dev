// SchoolRepository.java
package com.example.demo.repository;

import com.example.demo.model.School;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

public interface SchoolRepository extends JpaRepository<School, Long> {
    Optional<School> findBySchoolName(String school_name);

    @Query("SELECT s FROM School s WHERE LOWER(s.schoolName) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<School> findBySchoolNameContainingIgnoreCase(@Param("name") String name);

    @Query("SELECT s FROM School s WHERE s.user.user_id = :userId")
    Optional<School> findByUserId(@Param("userId") Long userId);
}