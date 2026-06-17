package com.example.it_iap.service;

import com.example.it_iap.entity.Question;
import com.example.it_iap.entity.enums.TargetLevel;
import com.example.it_iap.entity.enums.TargetPosition;

import java.util.List;

public interface AIService {
    List<Question> generateQuestion (int quantity, TargetLevel level, TargetPosition position);
}
