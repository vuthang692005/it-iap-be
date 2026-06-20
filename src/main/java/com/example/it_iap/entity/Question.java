package com.example.it_iap.entity;

import com.example.it_iap.entity.enums.*;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@Entity
public class Question extends Auditable{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String content;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String suggestedAnswer;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String hintContent;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private TargetPosition position;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private TargetLevel level;

    @Enumerated(EnumType.STRING)
    private QuestionType category;

    @JdbcTypeCode(SqlTypes.JSON)
    private Set<String> skillTag;

    private int timeLimitSeconds;

    @Enumerated(EnumType.STRING)
    private Source source;
    
    @Enumerated(EnumType.STRING)
    private QuestionStatus status;

    private LocalDateTime deleteAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "prompt_version_id")
    private PromptVersion promptVersion;

    @OneToMany(mappedBy = "question", fetch = FetchType.LAZY)
    private List<InterviewQuestion> interviewQuestions;
}
