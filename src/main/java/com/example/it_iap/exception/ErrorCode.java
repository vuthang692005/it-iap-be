package com.example.it_iap.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    //INVALID
    DATA_INVALID(400,"Dữ liệu không hợp lệ", HttpStatus.BAD_REQUEST),

    //BUSINESS_ERROR
    OTP_VERIFICATION_FAILED(400,"Xác thực OTP thất bại", HttpStatus.BAD_REQUEST),
    ACCOUNT_AWAITING_VERIFICATION(400, "Tài khoản đang chờ xác thực. Vui lòng kiểm tra email hoặc thử lại sau vài phút", HttpStatus.BAD_REQUEST),
    PROVIDER_MODEL_MISMATCH(400,"Mô hình AI không thuộc nhà cung cấp đã chọn",HttpStatus.BAD_REQUEST),
    OLD_PASSWORD_MISMATCH(400, "Mật khẩu cũ không chính xác", HttpStatus.BAD_REQUEST),
    UNABLE_TO_UPLOAD_IMAGE(400, "Cập nhật ảnh đại diện thất bại", HttpStatus.BAD_REQUEST),
    INVALID_IMAGE(400, "Ảnh không hợp lệ", HttpStatus.BAD_REQUEST),
    INVALID_IMAGE_SIZE(400, "Kích thước ảnh quá lớn", HttpStatus.BAD_REQUEST),
    INVALID_IMAGE_TYPE(400, "Định dạng ảnh không hỗ trợ", HttpStatus.BAD_REQUEST),
    EMAIL_ALREADY_USED(400, "Vui lòng nhập một email khác với email đang sử dụng", HttpStatus.BAD_REQUEST),
    INTERVIEW_STARTED(400, "Phiên phỏng vấn đã bắt đầu", HttpStatus.BAD_REQUEST),
    INTERVIEW_NOT_COMPLETED(400, "Phiên phỏng vấn chưa hoàn thành.", HttpStatus.BAD_REQUEST),
    INTERVIEW_NOT_IN_PROGRESS(400, "Phiên phỏng vấn chưa bắt đầu hoặc đã kết thúc", HttpStatus.BAD_REQUEST),
    QUESTION_NOT_ACTIVE(400,"Câu hỏi này đã được trả lời hoặc chưa được bắt đầu", HttpStatus.BAD_REQUEST),
    QUESTION_NOT_READY(400, "Câu hỏi chưa sẵn sàng để tương tác, vui lòng thử lại", HttpStatus.BAD_REQUEST),
    QUESTION_ALREADY_COMPLETED(400, "Câu hỏi này đã được hoàn thành", HttpStatus.BAD_REQUEST),
    AI_IS_RESPONDING(400, "AI đang trả lời", HttpStatus.BAD_REQUEST),
    INCOMPATIBLE_INTERVIEW_TYPE(400, "Loại hình phỏng vấn không tương thích", HttpStatus.BAD_REQUEST),
    CURRENT_QUESTION_NOT_ANSWERED(400, "Bạn cần hoàn thành câu hỏi hiện tại trước khi chuyển sang câu tiếp theo.", HttpStatus.BAD_REQUEST),
    TOKEN_LIMIT_EXCEEDED(400, "Đã vượt quá giới hạn token", HttpStatus.BAD_REQUEST),
    TWO_FACTOR_ENABLED(400, "Tài khoản đã bật xác minh 2 bước rồi", HttpStatus.BAD_REQUEST),
    TWO_FACTOR_NOT_ENABLED(400, "Tài khoản chưa bật xác minh 2 bước", HttpStatus.BAD_REQUEST),
    TWO_FACTOR_CODE_INVALID(400, "Mã xác thực 2 bước không hợp lệ hoặc chưa chính xác", HttpStatus.BAD_REQUEST),
    
    //EXISTED
    EMAIL_EXISTED(409, "Email đã được sử dụng", HttpStatus.CONFLICT),
    UNVERIFIED_ACCOUNT_EXISTS(409, "Tài khoản đã đăng ký nhưng chưa kích hoạt. Một mã OTP mới vừa được gửi đến email của bạn", HttpStatus.CONFLICT),
    VERSION_EXISTS(409, "Phiên bản đã tồn tại", HttpStatus.CONFLICT),
    PROMPT_KEY_EXISTS(409, "Prompt key đã tôn tại", HttpStatus.CONFLICT),

    //CONFLICT
    CONCURRENT_UPDATE(409, "Dữ liệu đang được xử lý bởi một yêu cầu khác, vui lòng không thao tác liên tục!", HttpStatus.CONFLICT),

    //NOT_FOUND
    USER_NOT_FOUND(404,"Người dùng không tồn tại" , HttpStatus.NOT_FOUND),
    PROFILE_NOT_FOUND(404,"Hồ sơ không tồn tại" , HttpStatus.NOT_FOUND),
    PROMPT_NOT_FOUND(404,"Prompt không tồn tại" , HttpStatus.NOT_FOUND),
    CHAT_SESSION_NOT_FOUND(404, "Phiên trò chuyện không tồn tại", HttpStatus.NOT_FOUND),
    QUESTION_NOT_FOUND(404, "Câu hỏi không tông tại", HttpStatus.NOT_FOUND),
    ACTIVE_PROMPT_NOT_FOUND(404, "Tính năng này hiện chưa được cấu hình phiên bản Prompt hoạt động", HttpStatus.NOT_FOUND),
    INTERVIEW_NOT_FOUND(404,"Phiên phỏng vấn không tồn tại" , HttpStatus.NOT_FOUND),
    QUESTION_INTERVIEW_NOT_FOUND(404, "Không tìm thấy câu hỏi hợp lệ" , HttpStatus.NOT_FOUND),
    CHAT_HISTORY_NOT_FOUND(404, "Không tìm thấy lịch sử chat", HttpStatus.NOT_FOUND),
    REPORT_NOT_FOUND(404,"Báo cáo không tồn tại", HttpStatus.NOT_FOUND),
    FEEDBACK_NOT_FOUND(404,"Đánh giá không tồn tại", HttpStatus.NOT_FOUND),
    BANNER_NOT_FOUND(404, "Banner không tồn tại", HttpStatus.NOT_FOUND),

    //UNAUTHENTICATED
    TOKEN_INVALID(401, "Thông tin xác thực không hợp lệ", HttpStatus.UNAUTHORIZED),
    UNAUTHENTICATED(401,"Thông tin đăng nhập không hợp lệ", HttpStatus.UNAUTHORIZED),
    AUTHENTICATION_FAILED(401,"Xác thực thất bại, vui lòng đăng nhập lại", HttpStatus.UNAUTHORIZED),
    REFRESH_TOKEN_EXPIRED(401,"Phiên đăng nhập đã hết hạn, vui lòng đăng nhập lại", HttpStatus.UNAUTHORIZED),

    // FORBIDDEN
    ACCOUNT_DISABLED(403,"Tài khoản đã bị vô hiệu hóa", HttpStatus.FORBIDDEN),
    ACCESS_DENIED(403, "Bạn không có quyền thực hiện hành động này", HttpStatus.FORBIDDEN),

    //SYSTEM_ERROR
    SYSTEM_ERROR(500,"Hệ thống đang gặp lỗi, vui lòng thử lại sau", HttpStatus.INTERNAL_SERVER_ERROR),
    ;

    private final int code;
    private final String message;
    private final HttpStatusCode statusCode;
}
