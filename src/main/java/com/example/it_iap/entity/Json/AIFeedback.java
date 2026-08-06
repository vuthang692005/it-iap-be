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
    @JsonPropertyDescription("Điểm số chuyên môn của câu hỏi dựa trên đáp án mẫu. Sẽ được dùng để tính Core Knowledge, Problem Solving, hoặc Applied Experience tùy theo loại câu hỏi. Chấm trên thang điểm 10, lẻ đến 0.5.")
    private Float point;

    @JsonPropertyDescription("Điểm đánh giá Tư duy trình bày (Logical Articulation) của ứng viên trong câu trả lời này: tính mạch lạc, cấu trúc rõ ràng, dễ hiểu. Chấm thang điểm 10, lẻ đến 0.5.")
    private Float articulationPoint;

    @JsonPropertyDescription("Điểm đánh giá Độ sâu & Trọng tâm (Focus & Completeness): trả lời đúng trọng tâm, không lan man, có đào sâu vấn đề. Chấm thang điểm 10, lẻ đến 0.5.")
    private Float focusPoint;

    @JsonPropertyDescription("Đoạn nhận xét tổng quan bằng tiếng Việt (tối đa 4 câu). Giải thích lý do chấm điểm chuyên môn, đồng thời nhận xét ngắn gọn về cách trình bày và mức độ bám sát trọng tâm của ứng viên.")
    private String feedback;
}
