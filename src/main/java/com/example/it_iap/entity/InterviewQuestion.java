package com.example.it_iap.entity;

import com.example.it_iap.entity.Json.AIFeedback;
import com.example.it_iap.entity.enums.InterviewQuestionStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
public class InterviewQuestion extends Auditable{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private int orderIndex; // STT các câu trong đề

    @Column(columnDefinition = "TEXT")
    private String userAnswer;

    private boolean hintUsed = false;

    private LocalDateTime completeAt;

    private LocalDateTime endAt;

    @JdbcTypeCode(SqlTypes.JSON)
    private AIFeedback aiFeedback;

    @Version
    private Integer version = 0;

    @Enumerated(EnumType.STRING)
    private InterviewQuestionStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "interview_id", nullable = false)
    private Interview interview;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id", nullable = false)
    private Question question;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "prompt_version_id", nullable = false)
    private PromptVersion promptVersion;

    @OneToOne(mappedBy = "interviewQuestion", cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private ChatSession chatSession;

    @OneToMany(mappedBy = "interviewQuestion", fetch = FetchType.LAZY)
    private List<Reports> reports;
}
