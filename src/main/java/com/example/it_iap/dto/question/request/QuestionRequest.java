package com.example.it_iap.dto.question.request;

import java.util.Set;

import com.example.it_iap.entity.enums.QuestionStatus;
import com.example.it_iap.entity.enums.QuestionType;
import com.example.it_iap.entity.enums.TargetLevel;
import com.example.it_iap.entity.enums.TargetPosition;
import com.example.it_iap.validator.annotation.EnumValue;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class QuestionRequest {
    @NotBlank(message = "CONTENT_INVALID")
    @Size(min = 4, max = 10000, message = "CONTENT_INVALID")
    String content;

    @Size(min = 4, max = 10000, message = "SUGGESTED_ANSWER_INVALID")
    @NotBlank(message = "SUGGESTED_ANSWER_INVALID")
    String suggestedAnswer;

    @Size(min = 4, max = 3636, message = "HINT_CONTENT_INVALID")
    @NotBlank(message = "HINT_CONTENT_INVALID")
    String hintContent;

    @NotBlank(message = "POSITION_INVALID")
    @EnumValue(enumClass = TargetPosition.class, message = "POSITION_INVALID")
    String position;

    @NotBlank(message = "LEVEL_INVALID")
    @EnumValue(enumClass = TargetLevel.class, message = "LEVEL_INVALID")
    String level;

    @EnumValue(enumClass = QuestionType.class, message = "CATEGORY_INVALID")
    String category;

    Set<String> skillTag;

    @NotNull(message = "TIME_LIMIT_SECONDS_INVALID")
    @Min(value = 10, message = "SIZE_VALUE_INVALID")
    @Max(value = 50, message = "SIZE_VALUE_INVALID")
    int timeLimitSeconds;

    @NotNull(message = "QUESTION_STATUS_INVALID")
    @EnumValue(enumClass = QuestionStatus.class, message = "QUESTION_STATUS_INVALID")
    String status;

    boolean delete; // dễ if else xử lý xóa
}
