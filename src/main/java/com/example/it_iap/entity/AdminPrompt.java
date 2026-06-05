package com.example.it_iap.entity;

import com.example.it_iap.entity.enums.PromptUseCase;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Getter
@Setter
@Table(
        indexes = {
                @Index(name = "idx_admin_prompt_apply_for", columnList = "apply_for")
        }
)
public class AdminPrompt extends Auditable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String promptKey;

    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, name = "apply_for")
    private PromptUseCase applyFor;

    @OneToMany(mappedBy = "adminPrompt", fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private List<PromptVersion> promptVersions;
}
