package com.example.it_iap.repository;

import com.example.it_iap.entity.Banner;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface BannerRepository extends JpaRepository<Banner, Long> {
    Optional<Banner> findFirstByIsActiveTrueOrderByIdDesc();

    @Modifying
    @Query("UPDATE Banner b SET b.isActive = false WHERE b.isActive = true")
    void deactivateAllBanners();
}