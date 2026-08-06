package com.example.it_iap.service.impl;

import com.example.it_iap.dto.promotion.request.CreatePromotionRequest;
import com.example.it_iap.dto.promotion.response.PromotionResponse;
import com.example.it_iap.dto.promotion.response.TierInfoResponse;
import com.example.it_iap.entity.Promotion;
import com.example.it_iap.entity.enums.AccountTier;
import com.example.it_iap.entity.enums.DiscountType;
import com.example.it_iap.exception.AppException;
import com.example.it_iap.exception.ErrorCode;
import com.example.it_iap.repository.PromotionRepository;
import com.example.it_iap.service.PromotionService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PromotionServiceImpl implements PromotionService {
    private final PromotionRepository promotionRepository;

    public Promotion validateAndGetPromotion(String code, AccountTier accountTier) {
        if (code == null || code.isBlank()) {
            return null; // Không truyền mã -> Không lỗi, chỉ là không có khuyến mãi
        }

        Promotion promotion = promotionRepository.findByCode(code)
                .orElseThrow(() -> new AppException(ErrorCode.PROMOTION_NOT_FOUND));

        LocalDateTime now = LocalDateTime.now();

        // 1. Kiểm tra trạng thái và hạn sử dụng
        if (!promotion.getIsActive() || now.isBefore(promotion.getStartDate()) || now.isAfter(promotion.getEndDate())) {
            throw new AppException(ErrorCode.PROMOTION_EXPIRED_OR_INACTIVE);
        }

        // 2. Kiểm tra mã có áp dụng đúng gói đang mua không
        if (promotion.getApplicableTier() != null && promotion.getApplicableTier() != accountTier) {
            throw new AppException(ErrorCode.PROMOTION_INVALID_FOR_TIER);
        }

        return promotion;
    }

    public Long calculateDiscount(Promotion promotion, Long originalPrice) {
        if (promotion == null) {
            return 0L;
        }

        Long discount = 0L;
        if (promotion.getDiscountType() == DiscountType.FIXED_AMOUNT) {
            discount = promotion.getDiscountValue();
        } else if (promotion.getDiscountType() == DiscountType.PERCENTAGE) {
            discount = (originalPrice * promotion.getDiscountValue()) / 100;
        }

        // Đảm bảo không giảm lố tiền gốc
        return Math.min(discount, originalPrice);
    }

    @Transactional
    public PromotionResponse createPromotion(CreatePromotionRequest request) {
        if (promotionRepository.existsByCode(request.getCode())) {
            throw new AppException(ErrorCode.PROMOTION_CODE_EXISTS);
        }

        if (request.getStartDate().isAfter(request.getEndDate())) {
            throw new AppException(ErrorCode.INVALID_DATE_RANGE);
        }

        AccountTier tier = AccountTier.from(request.getApplicableTier());
        DiscountType type = DiscountType.valueOf(request.getDiscountType());

        // 🌟 BỔ SUNG: Kiểm tra logic nếu giảm theo % thì không được vượt quá 100%
        if (type == DiscountType.PERCENTAGE && request.getDiscountValue() > 100) {
            throw new AppException(ErrorCode.DISCOUNT_PERCENTAGE_INVALID);
        }

        // Kiểm tra rule: Chỉ 1 khuyến mãi active cho 1 gói
        boolean hasOverlap = promotionRepository.hasOverlappingPromotionForTier(
                tier, request.getStartDate(), request.getEndDate(), null);

        if (hasOverlap) {
            throw new AppException(ErrorCode.PROMOTION_TIME_OVERLAPPING);
        }

        Promotion promotion = new Promotion();
        promotion.setCode(request.getCode());
        promotion.setName(request.getName());
        promotion.setDescription(request.getDescription());
        promotion.setApplicableTier(tier);
        promotion.setDiscountType(type);
        promotion.setDiscountValue(request.getDiscountValue());
        promotion.setStartDate(request.getStartDate());
        promotion.setEndDate(request.getEndDate());
        promotion.setIsActive(true);

        promotion = promotionRepository.save(promotion);

        return mapToResponse(promotion);
    }

    @Transactional
    public PromotionResponse toggleActiveStatus(Long promotionId) {
        Promotion promotion = promotionRepository.findById(promotionId)
                .orElseThrow(() -> new AppException(ErrorCode.PROMOTION_NOT_FOUND));

        // Nếu đang định BẬT nó lên, phải kiểm tra xem khoảng thời gian của nó
        // có đang dẫm chân lên thằng nào đang bật sẵn không.
        if (!promotion.getIsActive()) {
            boolean hasOverlap = promotionRepository.hasOverlappingPromotionForTier(
                    promotion.getApplicableTier(),
                    promotion.getStartDate(),
                    promotion.getEndDate(),
                    promotion.getId() // Loại trừ chính nó ra
            );

            if (hasOverlap) {
                throw new AppException(ErrorCode.PROMOTION_TIME_OVERLAPPING);
            }
        }

        // Đảo ngược trạng thái
        promotion.setIsActive(!promotion.getIsActive());
        promotion = promotionRepository.save(promotion);

        return mapToResponse(promotion);
    }

    // TÍNH NĂNG CHO USER: Xem danh sách các gói (Trừ BASIC) và khuyến mãi của nó
    public List<TierInfoResponse> getAvailableTiers() {
        return Arrays.stream(AccountTier.values())
                .filter(tier -> tier != AccountTier.BASIC) // Bỏ qua gói mặc định
                .map(tier -> {
                    // Cố gắng tìm khuyến mãi đang chạy của gói này
                    PromotionResponse activePromoResponse = promotionRepository
                            .findActivePromotionByTier(tier, LocalDateTime.now())
                            .map(this::mapToResponse)
                            .orElse(null);

                    return new TierInfoResponse(
                            tier.name(),
                            tier.getProductName(),
                            tier.getDescription(),
                            tier.getPrice(),
                            tier.getLevel(),
                            activePromoResponse
                    );
                })
                .collect(Collectors.toList());
    }

    public Page<PromotionResponse> getAllPromotions(int page) {
        int page1 = Math.max(0, page - 1);
        int size = 10;

        Pageable pageable = PageRequest.of(
                page1 - 1,
                size,
                Sort.by(Sort.Direction.DESC, "createdAt") // Sắp xếp mới nhất lên đầu
        );

        Page<Promotion> promotionPage = promotionRepository.findAll(pageable);
        return promotionPage.map(this::mapToResponse);
    }

    private PromotionResponse mapToResponse(Promotion promotion) {
        return new PromotionResponse(
                promotion.getId(),
                promotion.getCode(),
                promotion.getName(),
                promotion.getDescription(),
                promotion.getApplicableTier().name(),
                promotion.getDiscountType().name(),
                promotion.getDiscountValue(),
                promotion.getStartDate(),
                promotion.getEndDate(),
                promotion.getIsActive()
        );
    }
}
