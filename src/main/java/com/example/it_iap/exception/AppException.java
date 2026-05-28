package com.example.it_iap.exception;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AppException extends RuntimeException {
    private ErrorCode errorCode;
    private Object data;

    // Ném lỗi nghiệp vụ thông thường (chỉ có mã lỗi và thông báo)
    public AppException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    // Ném lỗi kèm dữ liệu (Data Payload) để hỗ trợ các luồng phức tạp
    public AppException(ErrorCode errorCode, Object data) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
        this.data = data;
    }
}
