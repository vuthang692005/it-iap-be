package com.example.it_iap.record;

import com.example.it_iap.enums.UserRank;

public record GradeSharedData (
    Double currentGpa,
    UserRank userRank,
    int totalCompletedInterviews
) {
    
}
