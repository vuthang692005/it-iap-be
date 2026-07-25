package com.example.it_iap.dto.feedback.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Range;

@Getter
@Setter
public class FeedbackFilterRequest {
    @Range(min = 1, max = 5, message = "RATING_INVALID")
    private Integer rating;
    private int page = 1;
    private Boolean onlyMine;
    private Boolean hasAdminReply;
    private Boolean hasImageUrl;
}
