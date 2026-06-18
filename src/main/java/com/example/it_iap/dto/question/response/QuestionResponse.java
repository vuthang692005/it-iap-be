package com.example.it_iap.dto.question.response;

import java.time.LocalDateTime;
import java.util.Set;

import com.example.it_iap.entity.enums.QuestionStatus;
import com.example.it_iap.entity.enums.QuestionType;
import com.example.it_iap.entity.enums.Source;
import com.example.it_iap.entity.enums.TargetLevel;
import com.example.it_iap.entity.enums.TargetPosition;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Setter
@Getter
@Builder
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class QuestionResponse {
    Long id;
    String content;
    String suggestedAnswer;
    String hintContent;
    TargetPosition position;
    TargetLevel level;
    QuestionType category;
    Set<String> skillTag;
    int timeLimitSeconds;
    Source source;
    QuestionStatus status;
    LocalDateTime deleteAt;
}
