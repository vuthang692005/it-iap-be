package com.example.it_iap.entity.Json;

import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OverallResult {
    @JsonPropertyDescription("Điểm số trung bình: Tính bằng trung bình cộng 'Điểm câu hỏi' của tất cả các câu hỏi. Nếu không có, trả về null..")
    private Float totalPoint;

    @JsonPropertyDescription("Điểm Kiến thức chuyên môn (Core Knowledge): Tính bằng trung bình cộng 'Điểm câu hỏi' của các câu mang phân loại TECHNICAL. Nếu không có, trả về null.")
    private Float coreKnowledge;

    @JsonPropertyDescription("Điểm Giải quyết vấn đề (Problem Solving): Tính bằng trung bình cộng 'Điểm câu hỏi' của các câu mang phân loại SITUATIONAL. Nếu không có, trả về null.")
    private Float problemSolving;

    @JsonPropertyDescription("Điểm Kinh nghiệm thực tiễn (Applied Experience): Tính bằng trung bình cộng 'Điểm câu hỏi' của các câu mang phân loại BEHAVIORAL. Nếu không có, trả về null.")
    private Float appliedExperience;

    @JsonPropertyDescription("Điểm Tư duy trình bày (Logical Articulation): Tính bằng trung bình cộng điểm 'articulationPoint' của tất cả các câu hỏi.")
    private Float logicalArticulation;

    @JsonPropertyDescription("Điểm Độ sâu & Trọng tâm (Focus & Completeness): Tính bằng trung bình cộng điểm 'focusPoint' của tất cả các câu hỏi.")
    private Float focusAndCompleteness;

    @JsonPropertyDescription("Đoạn nhận xét tổng quan bằng tiếng Việt (khoảng 3-5 câu), tóm tắt điểm mạnh, điểm yếu lớn nhất của ứng viên dựa trên 5 tiêu chí trên.")
    private String feedback;
}
