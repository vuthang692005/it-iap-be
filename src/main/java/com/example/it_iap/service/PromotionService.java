package com.example.it_iap.service;

import com.example.it_iap.dto.promotion.request.CreatePromotionRequest;
import com.example.it_iap.dto.promotion.response.PromotionResponse;
import com.example.it_iap.dto.promotion.response.TierInfoResponse;
import com.example.it_iap.entity.Promotion;
import com.example.it_iap.entity.enums.AccountTier;
import org.springframework.data.domain.Page;

import java.util.List;

public interface PromotionService {
    Promotion validateAndGetPromotion(String code, AccountTier accountTier);
    Long calculateDiscount(Promotion promotion, Long originalPrice);
    PromotionResponse createPromotion(CreatePromotionRequest request);
    PromotionResponse toggleActiveStatus(Long promotionId);
    List<TierInfoResponse> getAvailableTiers();
    Page<PromotionResponse> getAllPromotions(int page);
}
