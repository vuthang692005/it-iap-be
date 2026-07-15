package com.example.it_iap.entity;

import com.example.it_iap.entity.Json.DailyStudyStat;
import com.example.it_iap.entity.Json.ResumeData;
import com.example.it_iap.entity.enums.TargetLevel;
import com.example.it_iap.entity.enums.TargetPosition;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
public class Profile extends Auditable{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private TargetPosition targetPosition;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private TargetLevel targetLevel;

    @JdbcTypeCode(SqlTypes.JSON)
    private ResumeData resumeData;

    private LocalDateTime deletedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToMany(mappedBy = "profile", fetch = FetchType.LAZY)
    private List<Interview> interviews;
}
