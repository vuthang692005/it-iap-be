package com.example.it_iap.dto.ai.request;

import com.example.it_iap.entity.enums.TargetLevel;
import com.example.it_iap.entity.enums.TargetPosition;
import lombok.Getter;

@Getter
public class GenerateQuestionRequest {
    private int quantity;
    private TargetLevel level;
    private TargetPosition position;
}
