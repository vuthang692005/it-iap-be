package com.example.it_iap.repository;

import com.example.it_iap.entity.Interview;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface InterviewRepository extends JpaRepository<Interview, Long> {
    Optional<Interview> findByIdAndProfile_UserId(Long interviewId, UUID userId);

    @EntityGraph(attributePaths = {"interviewQuestions", "interviewQuestions.question", "promptVersion"})
    Optional<Interview> findWithInterviewQuestionsAndQuestionByIdAndProfile_UserId(Long interviewId, UUID userId);
}