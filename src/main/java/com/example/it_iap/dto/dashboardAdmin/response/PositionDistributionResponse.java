package com.example.it_iap.dto.dashboardAdmin.response;

import com.example.it_iap.entity.enums.TargetPosition;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PositionDistributionResponse {
    private TargetPosition position;
    private long totalInterviews;
}
