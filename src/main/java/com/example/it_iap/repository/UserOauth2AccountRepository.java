package com.example.it_iap.repository;

import com.example.it_iap.entity.UserOauth2Account;
import com.example.it_iap.entity.enums.AuthProvider;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserOauth2AccountRepository extends JpaRepository<UserOauth2Account, Long> {
    Optional<UserOauth2Account> findByProviderAndProviderId(AuthProvider provider, String providerId);

    @EntityGraph(attributePaths = {
            "user"
    })
    Optional<UserOauth2Account> findWithUserByProviderAndProviderId(AuthProvider provider, String providerId);
}
