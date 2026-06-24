package com.example.it_iap.dto.question.request;

import com.example.it_iap.entity.enums.QuestionType;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
public class AICreateQuestionRequest {
    private String content;
    private String suggestedAnswer;
    private String hintContent;
    private QuestionType category;
    private Set<String> skillTag;
    private int timeLimitSeconds;
}
