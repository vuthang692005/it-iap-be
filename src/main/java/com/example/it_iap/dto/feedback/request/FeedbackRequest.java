package com.example.it_iap.dto.feedback.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Range;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
public class FeedbackRequest {
    @Range(min = 1, max = 5, message = "RATING_INVALID")
    @NotNull(message = "RATING_INVALID")
    private Integer rating;

    private String content;

    private MultipartFile image;
}
