package com.example.it_iap.dto.order.request;

import com.example.it_iap.entity.enums.AccountTier;
import com.example.it_iap.validator.annotation.EnumValue;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class OrderRequest {
    @NotBlank(message = "ACCOUNT_TIER_INVALID")
    @EnumValue(enumClass = AccountTier.class, message = "ACCOUNT_TIER_INVALID")
    private String accountTier;

    @Min(value = 1, message = "Số lượng mua tối thiểu là 1")
    private Integer quantity = 1;

    private String promotionCode;

    @NotBlank(message = "RETURN_URL_INVALID")
    private String returnUrl;

    @NotBlank(message = "CANCEL_URL_INVALID")
    private String cancelUrl;
}
