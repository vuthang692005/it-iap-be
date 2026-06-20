package com.example.it_iap.entity.Json;

import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OverallResult {
    @JsonPropertyDescription("Điểm số của ứng viên, được chấm trên thang điểm 10. Chấm chi li đến 2 chữ số thập phân (VD: 7.75)")
    private float totalPoint;
    @JsonPropertyDescription("Đoạn nhận xét tổng quan bằng tiếng Việt, nêu điểm yếu/ mạnh của ứng viên.")
    private String feedback;
}
