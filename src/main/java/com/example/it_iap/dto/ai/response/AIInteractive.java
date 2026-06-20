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

    @JsonPropertyDescription("Điểm số của ứng viên, được chấm trên thang điểm 10. Chấm chi li đến 1 chữ số thập phân (VD: 7.5). Mặc định là null, chỉ chấm khi isComplete = true")
    private Float point;

    @JsonPropertyDescription("Nếu isComplete = false: Đặt một câu hỏi ngắn gọn xoáy vào điểm yếu của ứng viên dựa trên đáp án mẫu / tiêu chí. Nếu isComplete = true: Đưa ra nhận xét tổng kết đánh giá câu trả lời.")
    private String content;
}
