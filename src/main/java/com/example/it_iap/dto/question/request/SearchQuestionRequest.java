package com.example.it_iap.dto.question.request;

import com.example.it_iap.entity.enums.QuestionStatus;
import com.example.it_iap.entity.enums.QuestionType;
import com.example.it_iap.entity.enums.Source;
import com.example.it_iap.entity.enums.TargetLevel;
import com.example.it_iap.entity.enums.TargetPosition;
import com.example.it_iap.validator.annotation.EnumValue;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SearchQuestionRequest { //content, position, level, category, source, status
    String content;

    @EnumValue(enumClass = TargetPosition.class, message = "POSITION_INVALID")
    String position;

    @EnumValue(enumClass = TargetLevel.class, message = "LEVEL_INVALID")
    String level;

    @EnumValue(enumClass = QuestionType.class, message = "CATEGORY_INVALID")
    String category;

    @EnumValue(enumClass = Source.class, message = "QUESTION_SOURCE_INVALID")
    String source;

    @EnumValue(enumClass = QuestionStatus.class, message = "QUESTION_STATUS_INVALID")
    String status;

    @Min(value = 1, message = "PAGE_VALUE_INVALID")
    int page = 1;

    @Min(value = 10, message = "SIZE_VALUE_INVALID")
    @Max(value = 50, message = "SIZE_VALUE_INVALID")
    Integer size = 10;

    Boolean isDeleted;
}
