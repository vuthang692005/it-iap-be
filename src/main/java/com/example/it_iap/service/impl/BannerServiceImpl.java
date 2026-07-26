package com.example.it_iap.service.impl;

import com.example.it_iap.dto.banner.request.BannerRequest;
import com.example.it_iap.dto.banner.response.BannerResponse;
import com.example.it_iap.entity.Banner;
import com.example.it_iap.enums.UploadFolder;
import com.example.it_iap.exception.AppException;
import com.example.it_iap.exception.ErrorCode;
import com.example.it_iap.repository.BannerRepository;
import com.example.it_iap.service.BannerService;
import com.example.it_iap.service.CloudinaryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BannerServiceImpl implements BannerService {
    private final BannerRepository bannerRepository;
    private final CloudinaryService cloudinaryService;

    public Page<BannerResponse> getAllBanners(int page) {
        int page1 = Math.max(0, page - 1);
        int size = 10;

        Pageable pageable = PageRequest.of(page1, size, Sort.by("createdAt").descending());
        Page<Banner> bannerPage = bannerRepository.findAll(pageable);
        return bannerPage.map(this::mapToResponseDTO);
    }

    public BannerResponse getActiveBanner() {
        return bannerRepository.findFirstByIsActiveTrueOrderByIdDesc()
                .map(this::mapToResponseDTO)
                .orElse(null);
    }

    @Transactional
    public BannerResponse createBanner(BannerRequest request) {
        if (request.isActive()) {
            bannerRepository.deactivateAllBanners();
        }

        Banner banner = new Banner();

        if (request.getImage() != null && !request.getImage().isEmpty()) {
            String imageUrl = cloudinaryService.uploadImage(request.getImage(), UploadFolder.BANNER_IMAGE);
            banner.setImageUrl(imageUrl);
        }

        banner.setTitle(request.getTitle());
        banner.setContent(request.getContent());
        banner.setMarquee(request.getMarquee());
        banner.setActive(request.isActive());

        Banner savedBanner = bannerRepository.save(banner);
        return mapToResponseDTO(savedBanner);
    }

    @Transactional
    public BannerResponse updateBannerInfo(Long id, BannerRequest request) {
        Banner existingBanner = bannerRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.BANNER_NOT_FOUND));

        if (request.getImage() != null && !request.getImage().isEmpty()) {
            String newImageUrl = cloudinaryService.uploadImage(request.getImage(), UploadFolder.BANNER_IMAGE);
            existingBanner.setImageUrl(newImageUrl);
        }

        existingBanner.setTitle(request.getTitle());
        existingBanner.setContent(request.getContent());
        existingBanner.setMarquee(request.getMarquee());

        if (request.isActive() && !existingBanner.isActive()) {
            bannerRepository.deactivateAllBanners();
            existingBanner.setActive(true);
        } else if (!request.isActive()) {
            existingBanner.setActive(false);
        }

        Banner updatedBanner = bannerRepository.save(existingBanner);
        return mapToResponseDTO(updatedBanner);
    }

    @Transactional
    public void changeActiveStatus(Long id, boolean isActive) {
        Banner banner = bannerRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.BANNER_NOT_FOUND));

        if (isActive) {
            bannerRepository.deactivateAllBanners();
        }

        banner.setActive(isActive);
        bannerRepository.save(banner);
    }

    private BannerResponse mapToResponseDTO(Banner banner) {
        if (banner == null) return null;
        return new  BannerResponse(
                banner.getId(),
                banner.getTitle(),
                banner.getContent(),
                banner.getImageUrl(),
                banner.isActive(),
                banner.getMarquee()
        );
    }
}
