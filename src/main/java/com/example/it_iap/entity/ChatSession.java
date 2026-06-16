package com.example.it_iap.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Setter
@Getter
public class ChatSession extends Auditable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "interview_question_id", unique = true)
    private InterviewQuestion interviewQuestion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "prompt_version_id", nullable = false)
    private PromptVersion promptVersion;

    private String title;

    private int totalTokensUsed = 0;

    private Integer sessionLimitTokens = 16000;

    private LocalDateTime deleteAt;

    @OneToMany(mappedBy = "chatSession", fetch = FetchType.LAZY)
    private List<ChatMessage> chatMessages;
}
