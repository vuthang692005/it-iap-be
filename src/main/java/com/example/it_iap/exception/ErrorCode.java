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

    //EXISTED
    EMAIL_EXISTED(409, "Email đã tồn tại", HttpStatus.CONFLICT),
    UNVERIFIED_ACCOUNT_EXISTS(409, "Tài khoản đã đăng ký nhưng chưa kích hoạt. Một mã OTP mới vừa được gửi đến email của bạn", HttpStatus.CONFLICT),

    //NOT_FOUND
    USER_NOT_FOUND(404,"Người dùng không tồn tại" , HttpStatus.NOT_FOUND),

    //UNAUTHENTICATED
    UNAUTHENTICATED(401,"Thông tin đăng nhập không hợp lệ", HttpStatus.UNAUTHORIZED),
    AUTHENTICATION_FAILED(401,"Xác thực thất bại, vui lòng đăng nhập lại", HttpStatus.UNAUTHORIZED),
    REFRESH_TOKEN_EXPIRED(401,"Phiên đăng nhập đã hết hạn, vui lòng đăng nhập lại", HttpStatus.UNAUTHORIZED),

    // FORBIDDEN
    ACCOUNT_DISABLED(403,"Tài khoản đã bị vô hiệu hóa", HttpStatus.FORBIDDEN),

    //SYSTEM_ERROR
    SYSTEM_ERROR(500,"Hệ thống đang gặp lỗi, vui lòng thử lại sau", HttpStatus.INTERNAL_SERVER_ERROR),
    ;

    private final int code;
    private final String message;
    private final HttpStatusCode statusCode;
}
