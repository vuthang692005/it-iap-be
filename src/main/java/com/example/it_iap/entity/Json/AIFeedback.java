package com.example.it_iap.entity.Json;

import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AIFeedback {
    @JsonPropertyDescription("Điểm số của ứng viên, được chấm trên thang điểm 10. Chấm chi li đến 1 chữ số thập phân (VD: 7.5)")
    private Float point;

    @JsonPropertyDescription("Đoạn nhận xét tổng quan bằng tiếng Việt, giải thích vì sao lại chấm mức điểm đó, dài tối đa 3 câu.")
    private String feedback;
}
