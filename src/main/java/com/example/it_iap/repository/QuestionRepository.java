package com.example.it_iap.repository;

import com.example.it_iap.entity.Question;
import com.example.it_iap.entity.enums.QuestionType;
import com.example.it_iap.entity.enums.TargetLevel;
import com.example.it_iap.entity.enums.TargetPosition;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface QuestionRepository extends JpaRepository<Question, Long> {
    @Query(value = "SELECT * FROM question q " +
            "WHERE q.level = :#{#level.name()} " +
            "AND q.position = :#{#position.name()} " +
            "AND q.category = :#{#category.name()} " +
            "AND q.status = 'APPROVED' " +
            "AND q.delete_at IS NULL " +
            "ORDER BY RAND() " +
            "LIMIT :limit",
            nativeQuery = true)
    List<Question> findRandomQuestions(@Param("level") TargetLevel level,
                                       @Param("position") TargetPosition position,
                                       @Param("category") QuestionType category,
                                       @Param("limit") int limit);
}
