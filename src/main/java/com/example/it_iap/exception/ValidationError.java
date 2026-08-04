package com.example.it_iap.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Getter
public enum ValidationError {
    CONTENT_INVALID("Nội dung câu hỏi không hợp lệ"),
    SUGGESTED_ANSWER_INVALID("Câu trả lời đề xuất không hợp lệ"),
    HINT_CONTENT_INVALID("Nội dung gợi ý không hợp lệ"),
    POSITION_INVALID("Vị trí phỏng vấn (position) không hợp lệ"),
    LEVEL_INVALID("Vị trí phỏng vấn (level) không hợp lệ"),
    CATEGORY_INVALID("Kiểu câu hỏi không hợp lệ"),
    SOURCE_INVALID("Nguồn câu hỏi không hợp lệ"),
    QUESTION_STATUS_INVALID("Trạng thái câu hỏi không hợp lệ"),
    TIME_LIMIT_SECONDS_INVALID("Giới hạn thời gian không hợp lệ"),
    QUESTION_SOURCE_INVALID("Nguồn câu hỏi không hợp lệ"),
    PAGE_VALUE_INVALID("Giá trị số trang không hợp lệ"),
    SIZE_VALUE_INVALID("Giá trị kích thước trang không hợp lệ"),
    PASSWORD_INVALID("Mật khấu không hợp lệ"),
    EMAIL_INVALID("Email không hợp lệ"),
    FULL_NAME_INVALID("Họ tên không hợp lệ"),
    USER_ID_INVALID("Id người dùng không hợp lệ"),
    OTP_INVALID("Mã OTP không hợp lệ"),
    PHONE_NUMBER_INVALID("Số điện thoại không hợp lệ"),
    TITLE_INVALID("Tiêu đề không hợp lệ"),
    TARGET_POSITION_INVALID("Vị trí mục tiêu không hợp lệ"),
    TARGET_LEVEL_INVALID("Cấp độ mục tiêu không hợp lệ"),
    SKILL_NAME_INVALID("Tên kỹ năng không được để trống"),
    YEARS_EXPERIENCE_INVALID("Số năm kinh nghiệm không được nhỏ hơn 0"),
    EXPERIENCE_POSITION_INVALID("Vị trí kinh nghiệm không được để trống"),
    PROJECT_NAME_INVALID("Tên dự án không được để trống"),
    PROMPT_KEY_INVALID("Prompt key không hợp lệ"),
    VERSION_INVALID("Version không hợp lệ"),
    PROVIDER_INVALID("Provider không hợp lệ"),
    MODEL_INVALID("Model không hợp lệ"),
    PROMPT_CONTENT_INVALID("Nội dung prompt không được để trống"),
    APPLY_FOR_INVALID("Tính năng không hợp lệ"),
    PROMPT_USE_CASE_INVALID("Mục đích sử dụng Prompt không hợp lệ. Vui lòng chọn các giá trị được hỗ trợ."),
    AVATAR_URL_INVALID("Đường dẫn ảnh không hợp lệ"),
    INTERVIEW_MODE_INVALID("Chế độ phỏng vấn không hợp lệ hoặc bị trống"),
    PROFILE_ID_INVALID("Profile ID không được để trống"),
    USER_ANSWER_INVALID("Câu trả lời không được để trống"),
    QUESTION_QUANTITY_INVALID("Số lượng câu hỏi phải lớn hơn 1 và nhỏ hơn 10"),
    REPORT_TYPE_INVALID("Loại báo cáo không hợp lệ"),
    DESCRIPTION_INVALID("Mô tả không hợp lệ"),
    REPORT_STATUS_INVALID("Trạng thái báo cáo không hợp lệ"),
    ADMIN_REPLY_INVALID("Phản hồi admin không hợp lệ"),
    INTERVIEW_STATUS_INVALID("Trạng thái buổi phỏng vấn không hợp lệ"),
    USER_MESSAGE_INVALID("Câu hỏi không được để trống"),
    TWO_FACTOR_CODE_INVALID("Mã xác thực 2 bước không hợp lệ"),
    RATING_INVALID("Điểm đánh giá phải trong khoảng từ 1 đến 5"),
    BANNER_CONTENT_INVALID("Nội dung banner không hợp lệ"),
    INVALID_NOTIFICATION_TITLE("Tiêu đề thông báo không hợp lệ"),
    INVALID_NOTIFICATION_CONTENT("Nội dung thông báo không hợp lệ"),
    REACTYPE_INVALID("Loại cảm xúc không hợp lệ"),
    ;
    private final String message;

    private static final Map<String, ValidationError> LOOKUP =
            Arrays.stream(values())
                    .collect(Collectors.toMap(Enum::name, Function.identity()));

    public static Optional<ValidationError> from(String key) {
        return Optional.ofNullable(LOOKUP.get(key));
    }
}
