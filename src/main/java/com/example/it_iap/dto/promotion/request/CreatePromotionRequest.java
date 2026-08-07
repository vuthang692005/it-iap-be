package com.example.it_iap.dto.promotion.request;

import com.example.it_iap.entity.enums.AccountTier;
import com.example.it_iap.entity.enums.DiscountType;
import com.example.it_iap.validator.annotation.EnumValue;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class CreatePromotionRequest {
    @NotBlank(message = "PROMOTION_CODE_BLANK")
    private String code;

    @NotBlank(message = "PROMOTION_NAME_BLANK")
    private String name;

    private String description;

    @NotBlank(message = "ACCOUNT_TIER_INVALID")
    @EnumValue(enumClass = AccountTier.class, message = "ACCOUNT_TIER_INVALID")
    private String applicableTier;

    @NotBlank(message = "DISCOUNT_TYPE_INVALID")
    @EnumValue(enumClass = DiscountType.class, message = "DISCOUNT_TYPE_INVALID")
    private String discountType;

    @NotNull(message = "DISCOUNT_VALUE_NULL")
    @Min(value = 1, message = "DISCOUNT_VALUE_MIN")
    private Long discountValue;

    @NotNull(message = "START_DATE_NULL")
    private LocalDateTime startDate;

    @NotNull(message = "END_DATE_NULL")
    private LocalDateTime endDate;
}
