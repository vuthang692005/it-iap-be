package com.example.test.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    //INVALID (1xxx)
    DATA_INVALID(1001,"Dữ liệu không hợp lệ", HttpStatus.BAD_REQUEST),
    IDENTIFIER_INVALID(1002,"Tên đăng nhập hoặc email không hợp lệ", HttpStatus.BAD_REQUEST),
    PASSWORD_INVALID(1003,"Mật khấu không hợp lệ", HttpStatus.BAD_REQUEST),
    USERNAME_INVALID(1004,"Tên đăng nhập không hợp lệ", HttpStatus.BAD_REQUEST),
    EMAIL_INVALID(1005,"Email không hợp lệ", HttpStatus.BAD_REQUEST),
    FULL_NAME_INVALID(1006,"Họ tên không hợp lệ", HttpStatus.BAD_REQUEST),

    //EXISTED (2xxx)
    USERNAME_EXISTED(2001,"Tên đăng nhập đã tồn tại", HttpStatus.CONFLICT),
    EMAIL_EXISTED(2002, "Email đã tồn tại", HttpStatus.CONFLICT),
    USERNAME_OR_EMAIL_EXISTED(2003,"Tên đăng nhập hoặc email đã tồn tại", HttpStatus.CONFLICT),

    //NOT_FOUND (3xxx)

    //UNAUTHENTICATED (4xxx)
    UNAUTHENTICATED(4001,"Tên đăng nhập hoặc mật khẩu không đúng", HttpStatus.UNAUTHORIZED),
    AUTHENTICATION_FAILED(4002,"Xác thực thất bại, vui lòng đăng nhập lại", HttpStatus.UNAUTHORIZED),
    REFRESH_TOKEN_EXPIRED(4003,"Phiên đăng nhập đã hết hạn, vui lòng đăng nhập lại", HttpStatus.UNAUTHORIZED),

    // FORBIDDEN (5xxx)
    ACCOUNT_DISABLED(5001,"Tài khoản đã bị vô hiệu hóa", HttpStatus.FORBIDDEN),

    //SYSTEM_ERROR (6xxx)
    SYSTEM_ERROR(6001,"Hệ thống đang gặp lỗi, vui lòng thử lại sau", HttpStatus.INTERNAL_SERVER_ERROR),
    ;

    private final int code;
    private final String message;
    private final HttpStatusCode statusCode;
}
