package com.example.it_iap.repository;

import com.example.it_iap.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
    boolean existsByEmail(String email);
    Optional<User> findByEmail(String email);

    @Query("SELECT u FROM User u WHERE " +
            "(:email IS NULL OR TRIM(:email) = '' OR LOWER(u.email) LIKE LOWER(CONCAT('%', :email, '%'))) AND " +
            "(:fullName IS NULL OR TRIM(:fullName) = '' OR LOWER(u.fullName) LIKE LOWER(CONCAT('%', :fullName, '%'))) AND " +
            "(:phoneNumber IS NULL OR TRIM(:phoneNumber) = '' OR u.phoneNumber LIKE CONCAT('%', :phoneNumber, '%')) AND " +
            "CAST(u.roles AS string) NOT LIKE '%ADMIN%'")
    Page<User> searchUsers(
            @Param("email") String email,
            @Param("fullName") String fullName,
            @Param("phoneNumber") String phoneNumber,
            Pageable pageable
    );
}
