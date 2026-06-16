package com.example.it_iap.repository;

import com.example.it_iap.entity.Profile;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProfileRepository extends JpaRepository<Profile, Long> {

    @EntityGraph(
            attributePaths = {"user"}
    )
    Optional<Profile> findWithUserByIdAndDeletedAtIsNull(Long id);

    List<Profile> findAllByUserIdAndDeletedAtIsNull(UUID userId);
}
