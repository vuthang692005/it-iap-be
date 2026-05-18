package com.example.test.repository;

import com.example.test.entity.UserOauth2Account;
import com.example.test.entity.enums.AuthProvider;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserOauth2AccountRepository extends JpaRepository<UserOauth2Account, Long> {
    Optional<UserOauth2Account> findByProviderAndProviderId(AuthProvider provider, String providerId);

    @EntityGraph(attributePaths = {
            "user",
            "user.roles",
            "user.roles.permissions"
    })
    Optional<UserOauth2Account> findWithUserAndRolesByProviderAndProviderId(AuthProvider provider, String providerId);
}
