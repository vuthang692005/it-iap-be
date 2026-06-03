package com.example.it_iap.entity;

import com.example.it_iap.entity.enums.Role;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
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
public class User extends Auditable{
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @JdbcTypeCode(SqlTypes.BINARY)
    @Column(columnDefinition = "BINARY(16)")
    private UUID id;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(name = "password_hash")
    private String password;

    private String salt;

    private String fullName;

    @Column(unique = true)
    private String phoneNumber;

    private String avatarUrl;

    @Column(length = 50)
    private String authProvider = "local";

    @Column(length = 20)
    private String status = "active";

    private LocalDateTime deletedAt;

    private boolean isActive = true;

    private boolean isVerifyEmail = false;

    @JdbcTypeCode(SqlTypes.JSON)
    @Enumerated(EnumType.STRING)
    private Set<Role> roles;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<UserOauth2Account> userOauth2Accounts;
}
