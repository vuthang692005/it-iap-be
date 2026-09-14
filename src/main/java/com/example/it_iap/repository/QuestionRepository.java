package com.example.it_iap.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.example.it_iap.entity.Question;
import com.example.it_iap.entity.enums.QuestionType;
import com.example.it_iap.entity.enums.TargetLevel;
import com.example.it_iap.entity.enums.TargetPosition;
import com.example.it_iap.entity.enums.QuestionStatus;
import com.example.it_iap.entity.enums.Source;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface QuestionRepository extends JpaRepository<Question, Long> {

    @Query("""
        SELECT q
        FROM Question q
        WHERE (:content IS NULL OR LOWER(q.content) LIKE LOWER(CONCAT('%', :content, '%')))
          AND (:position IS NULL OR q.position = :position)
          AND (:level IS NULL OR q.level = :level)
          AND (:category IS NULL OR q.category = :category)
          AND (:source IS NULL OR q.source = :source)
          AND (:status IS NULL OR q.status = :status)
          AND (:isDeleted IS NULL\s
                     OR (:isDeleted = true AND q.deleteAt IS NOT NULL)\s
                     OR (:isDeleted = false AND q.deleteAt IS NULL))
        """)
    Page<Question> searchQuestions(
            @Param("content") String content,
            @Param("position") TargetPosition position,
            @Param("level") TargetLevel level,
            @Param("category") QuestionType category,
            @Param("source") Source source,
            @Param("status") QuestionStatus status,
            @Param("isDeleted") Boolean isDeleted,
            Pageable pageable
    );

    @Query("SELECT q.content FROM Question q WHERE q.level = :level AND q.position = :position")
    List<String> findContentByLevelAndPosition(
            @Param("level") TargetLevel level,
            @Param("position") TargetPosition position
    );

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
