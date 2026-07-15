package com.example.it_iap.dto.ai.response;

import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
public class AIInteractive {
    @JsonPropertyDescription("Trả về false nếu bạn cần hỏi thêm ứng viên. Bật thành true khi bạn thấy đã đủ thông tin để kết thúc phần tương tác cho câu hỏi hiện tại.")
    private Boolean isComplete;

    @JsonPropertyDescription("NẾU isComplete = false, BẮT BUỘC TRẢ VỀ null. NẾU isComplete = true, chấm điểm chuyên môn dựa trên đáp án mẫu (thang 10, lẻ đến 0.5, ví dụ 7.5).")
    private Float point;

    @JsonPropertyDescription("NẾU isComplete = false, BẮT BUỘC TRẢ VỀ null. NẾU isComplete = true, chấm điểm Tư duy trình bày (sự mạch lạc, logic, thang 10, lẻ đến 0.5).")
    private Float articulationPoint;

    @JsonPropertyDescription("NẾU isComplete = false, BẮT BUỘC TRẢ VỀ null. NẾU isComplete = true, chấm điểm Độ sâu & Trọng tâm (mức độ trả lời trúng đích, thang 10, lẻ đến 0.5).")
    private Float focusPoint;

    @JsonPropertyDescription("Nếu isComplete = false: Đặt một câu hỏi ngắn gọn xoáy vào điểm yếu của ứng viên dựa trên đáp án mẫu / tiêu chí. Nếu isComplete = true: Đưa ra nhận xét tổng kết đánh giá câu trả lời (cách trình bày và độ bám sát trọng tâm).")
    private String content;
}
