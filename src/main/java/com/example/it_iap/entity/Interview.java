package com.example.it_iap.entity;

import com.example.it_iap.entity.Json.OverallResult;
import com.example.it_iap.entity.enums.InterviewMode;
import com.example.it_iap.entity.enums.InterviewStatus;
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
public class Interview extends Auditable{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private InterviewMode mode;

    @Enumerated(EnumType.STRING)
    private InterviewStatus status;

    private LocalDateTime startAt;

    private LocalDateTime completedAt;

    @JdbcTypeCode(SqlTypes.JSON)
    private OverallResult overallResult;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "prompt_version_id", nullable = false)
    private PromptVersion promptVersion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "profile_id", nullable = false)
    private Profile profile;

    @OneToMany(mappedBy = "interview", fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
    private List<InterviewQuestion> interviewQuestions;

    @Version
    private Integer version = 0;
}
