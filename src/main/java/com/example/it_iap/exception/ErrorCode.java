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
    TOKEN_LIMIT_EXCEEDED(400, "Đã vượt quá giới hạn token của phiên chat này", HttpStatus.BAD_REQUEST),
    TWO_FACTOR_ENABLED(400, "Tài khoản đã bật xác minh 2 bước rồi", HttpStatus.BAD_REQUEST),
    TWO_FACTOR_NOT_ENABLED(400, "Tài khoản chưa bật xác minh 2 bước", HttpStatus.BAD_REQUEST),
    TWO_FACTOR_CODE_INVALID(400, "Mã xác thực 2 bước không hợp lệ hoặc chưa chính xác", HttpStatus.BAD_REQUEST),
    PAYMENT_NOT_REQUIRED(400, "Gói Miễn phí không yêu cầu thanh toán", HttpStatus.BAD_REQUEST),
    PROMOTION_EXPIRED_OR_INACTIVE(400, "chương trình khuyến mãi đã hết hạn hoặc không còn hiệu lực.", HttpStatus.BAD_REQUEST),
    PROMOTION_INVALID_FOR_TIER(400, "Mã khuyến mãi này không áp dụng cho gói tài khoản bạn đang chọn", HttpStatus.BAD_REQUEST),
    WEBHOOK_SIGNATURE_INVALID(400, "Chữ ký Webhook PayOS không hợp lệ hoặc dữ liệu bị giả mạo!", HttpStatus.BAD_REQUEST),
    INVALID_DATE_RANGE(400, "Thời gian bắt đầu không được lớn hơn thời gian kết thúc.", HttpStatus.BAD_REQUEST),
    PROMOTION_TIME_OVERLAPPING(400, "Thời gian áp dụng của khuyến mãi này bị trùng lặp với một khuyến mãi khác đang hoạt động của cùng gói cước", HttpStatus.BAD_REQUEST),
    INVALID_UPGRADE_TIER(400, "Bạn chỉ có thể nâng cấp lên gói cước cao hơn gói hiện tại.", HttpStatus.BAD_REQUEST),
    PROFILE_LIMIT_EXCEEDED(400, "Bạn đã đạt giới hạn số lượng hồ sơ cho phép của gói cước hiện tại. Vui lòng nâng cấp gói để tạo thêm.", HttpStatus.BAD_REQUEST),
    DAILY_INTERVIEW_LIMIT_EXCEEDED(400, "Bạn đã hết số lượt phỏng vấn trong ngày. Vui lòng quay lại vào ngày mai hoặc nâng cấp gói cước", HttpStatus.BAD_REQUEST),
    PROFILE_LOCKED_DUE_TO_DOWNGRADE(400, "Hồ sơ này đã bị khóa do vượt quá giới hạn của gói cước hiện tại. Vui lòng nâng cấp gói hoặc xóa bớt các hồ sơ cũ để sử dụng", HttpStatus.BAD_REQUEST),
    RESET_2FA_TOKEN_INVALID(400, "Đường dẫn yêu cầu khôi phục 2FA không hợp lệ hoặc đã hết hạn", HttpStatus.BAD_REQUEST),
    RESET_2FA_PENDING(400, "Bạn đã gửi một yêu cầu gỡ 2FA trước đó. Vui lòng kiểm tra email hoặc thử lại sau 10 phút", HttpStatus.BAD_REQUEST),
    RESET_2FA_SCHEDULED(400, "Tài khoản của bạn đang trong tiến trình đếm ngược gỡ 2FA. Vui lòng kiểm tra email nếu muốn hủy yêu cầu", HttpStatus.BAD_REQUEST),
    CURRENT_STREAK_NOT_ENOUGH(400, "Chuỗi hiện tại chưa đủ để chia sẻ, hãy cố gắng cải thiện thêm nhé", HttpStatus.BAD_REQUEST),
    CURRENT_GPA_TOO_LOW(400, "Điểm GPA hiện tại hơi thấp để chia sẻ, hãy cố gắng cải thiện thêm nhé", HttpStatus.BAD_REQUEST),
    NOT_ABLE_TO_REACT(400, "Bạn không thể thả cảm xúc với bài viết này", HttpStatus.BAD_REQUEST),
    DISCOUNT_PERCENTAGE_INVALID(400,"Giá trị giảm theo phần trăm không được vượt quá 100", HttpStatus.BAD_REQUEST),
    DISCOUNT_AMOUNT_EXCEEDS_PRICE(400,"Số tiền giảm giá phải nhỏ hơn giá gốc của gói cước", HttpStatus.BAD_REQUEST),
    DAILY_FEEDBACK_LIMIT_EXCEEDED(400, "Bạn chỉ được gửi tối đa 3 đánh giá mỗi ngày. Vui lòng quay lại vào ngày mai!", HttpStatus.BAD_REQUEST),

    //EXISTED
    EMAIL_EXISTED(409, "Email đã được sử dụng", HttpStatus.CONFLICT),
    UNVERIFIED_ACCOUNT_EXISTS(409, "Tài khoản đã đăng ký nhưng chưa kích hoạt. Một mã OTP mới vừa được gửi đến email của bạn", HttpStatus.CONFLICT),
    VERSION_EXISTS(409, "Phiên bản đã tồn tại", HttpStatus.CONFLICT),
    PROMPT_KEY_EXISTS(409, "Prompt key đã tôn tại", HttpStatus.CONFLICT),
    YOU_ALREADY_SHARE_TODAY(409, "Hôm nay bạn đã chia sẻ rồi", HttpStatus.CONFLICT),
    PROMOTION_CODE_EXISTS(409, "Mã khuyến mãi này đã tồn tại", HttpStatus.CONFLICT),


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
    SESSION_NOT_FOUND(404, "Phiên đăng nhập không tồn tại", HttpStatus.NOT_FOUND),
    POST_NOT_FOUND(404, "Bài đăng không tồn tại", HttpStatus.NOT_FOUND),
    PROMOTION_NOT_FOUND(404, "Mã khuyến mại không tồn tại", HttpStatus.NOT_FOUND),
    ORDER_NOT_FOUND(404, "Mã đơn hàng không tồn tại", HttpStatus.NOT_FOUND),

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
