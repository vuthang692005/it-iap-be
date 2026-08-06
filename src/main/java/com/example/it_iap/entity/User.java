package com.example.it_iap.entity;

import com.example.it_iap.entity.Json.DailyStudyStat;
import com.example.it_iap.entity.enums.AccountTier;
import com.example.it_iap.entity.enums.Role;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "users")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class User extends Auditable {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @JdbcTypeCode(SqlTypes.BINARY)
    @Column(columnDefinition = "BINARY(16)")
    private UUID id;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(name = "password_hash")
    private String password;

    private String fullName;

    private String phoneNumber;

    private String avatarUrl;

    private LocalDateTime deletedAt;

    private boolean isActive = true;

    private boolean isVerifyEmail = false;

    private boolean enable2fa = false;

    private String secret2fa;

    private LocalDateTime scheduled2faDisableAt;

    private Integer currentStreak = 0;

    private Integer longestStreak = 0;

    private LocalDateTime lastInterviewDate;

    private Integer totalCompletedInterviews = 0;

    private Double currentGpa = 0.0;

    @JdbcTypeCode(SqlTypes.JSON)
    private List<DailyStudyStat> dailyStudyStats = new ArrayList<>();

    @JdbcTypeCode(SqlTypes.JSON)
    @Enumerated(EnumType.STRING)
    private Set<Role> roles;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private List<UserOauth2Account> userOauth2Accounts;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private List<Profile> profiles;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private List<ChatSession> chatSessions;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private List<Reports> reports;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private List<Feedback> feedbacks;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private List<AdminActivityLog> adminActivityLogs;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private List<Order> orders;

    @OneToOne(mappedBy = "user")
    private UserSubscription userSubscription;

    @Transient
    public AccountTier getActiveTier() {
        // Nếu user chưa từng mua gói nào -> Trả về BASIC
        if (this.userSubscription == null) {
            return AccountTier.BASIC;
        }
        // Nếu có mua, nhờ class UserSubscription tự đánh giá xem còn hạn không
        return this.userSubscription.getActiveTier();
    }

    @Transient
    public LocalDateTime getSubscriptionEndDate() {
        // Nếu không có gói, hoặc gói hiện tại là BASIC (chưa mua hoặc đã hết hạn) -> Không có ngày hết hạn
        if (this.userSubscription == null || this.getActiveTier() == AccountTier.BASIC) {
            return null;
        }

        return this.userSubscription.getEndDate();
    }
}
