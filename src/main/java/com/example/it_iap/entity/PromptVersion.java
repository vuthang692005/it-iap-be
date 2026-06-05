package com.example.it_iap.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(
        name = "prompt_version",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_prompt_version",
                        columnNames = {"admin_prompt_id", "version"}
                )
        },
        indexes = {
                @Index(
                        name = "idx_prompt_version_active_time",
                        columnList = "last_activated_at"
                )
        }
)
    public class PromptVersion extends Auditable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String version;

    @Column(nullable = false)
    private String provider;

    @Column(nullable = false)
    private String model;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String promptContent;

    @Column(columnDefinition = "TEXT")
    private String note;

    @Column(name = "last_activated_at")
    private LocalDateTime lastActivatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "admin_prompt_id", nullable = false)
    private AdminPrompt adminPrompt;
}
