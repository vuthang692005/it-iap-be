package com.example.it_iap.dto.ai.request;

import com.example.it_iap.entity.enums.TargetLevel;
import com.example.it_iap.entity.enums.TargetPosition;
import com.example.it_iap.validator.annotation.EnumValue;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class GenerateQuestionRequest {

    @Min(value = 0, message = "QUESTION_QUANTITY_INVALID")
    @Max(value = 21, message = "QUESTION_QUANTITY_INVALID")
    private int quantity;

    @EnumValue(enumClass = TargetLevel.class, message = "TARGET_LEVEL_INVALID")
    @NotBlank(message = "TARGET_LEVEL_INVALID")
    private String level;

    @EnumValue(enumClass = TargetPosition.class, message = "TARGET_POSITION_INVALID")
    @NotBlank(message = "TARGET_POSITION_INVALID")
    private String position;
}
