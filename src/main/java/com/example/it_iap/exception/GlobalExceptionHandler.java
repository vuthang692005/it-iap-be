package com.example.it_iap.exception;

import com.example.it_iap.dto.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.http.ResponseEntity;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;

@Slf4j(topic = "GlobalExceptionHandler")
@ControllerAdvice
public class GlobalExceptionHandler {
    // Bắt các lỗi Runtime không mong muốn
    @ExceptionHandler(value = RuntimeException.class)
    ResponseEntity<ApiResponse> handlingRuntimeException(RuntimeException exception, HttpServletRequest request){
        ErrorCode errorCode = ErrorCode.SYSTEM_ERROR;
        log.error("path: {}", request.getRequestURI(), exception);
        return ResponseEntity.status(errorCode.getStatusCode())
                .body(ApiResponse.builder()
                        .code(errorCode.getCode())
                        .message(errorCode.getMessage())
                        .build());
    }

    // Xử lý lỗi khi cấu trúc Token không đúng định dạng hoặc không thể giải mã.
    @ExceptionHandler(value = ParseException.class)
    ResponseEntity<ApiResponse> handlingParseException(ParseException exception){
        ErrorCode errorCode = ErrorCode.AUTHENTICATION_FAILED;
        return ResponseEntity.status(errorCode.getStatusCode())
                .body(ApiResponse.builder()
                        .code(errorCode.getCode())
                        .message(errorCode.getMessage())
                        .build());
    }

    // Xử lý riêng lỗi kết nối Redis (Cache/OTP).
    @ExceptionHandler(RedisConnectionFailureException.class)
    public ResponseEntity<ApiResponse> handleRedisError(
            RedisConnectionFailureException exception, HttpServletRequest request) {
        ErrorCode errorCode = ErrorCode.SYSTEM_ERROR;
        log.error("Lỗi kết nối redis. Path: {}", request.getRequestURI(), exception);
        return ResponseEntity.status(errorCode.getStatusCode())
                .body(ApiResponse.builder()
                        .code(errorCode.getCode())
                        .message(errorCode.getMessage())
                        .build());
    }

    @ExceptionHandler(value = ObjectOptimisticLockingFailureException.class)
    ResponseEntity<ApiResponse> handlingOptimisticLockingException(ObjectOptimisticLockingFailureException exception) {
        ErrorCode errorCode = ErrorCode.CONCURRENT_UPDATE;
        return ResponseEntity.status(errorCode.getStatusCode())
                .body(ApiResponse.builder()
                        .code(errorCode.getCode())
                        .message(errorCode.getMessage())
                        .build());
    }

    // Xử lý các lỗi nghiệp vụ do chính ta ném ra chủ động (AppException).
    // Tự động bóc tách errorCode và data bổ sung (nếu có) để trả về cho Frontend.
    @ExceptionHandler(value = AppException.class)
    ResponseEntity<ApiResponse> handlingAppException(AppException exception){
        ErrorCode errorCode = exception.getErrorCode();
        Object data = exception.getData();
        return ResponseEntity.status(errorCode.getStatusCode())
                .body(ApiResponse.builder()
                        .code(errorCode.getCode())
                        .message(errorCode.getMessage())
                        .data(data)
                        .build());
    }

    // Xử lý lỗi liên quan tới bảo mật API (403)
    @ExceptionHandler(value = AuthorizationDeniedException.class)
    ResponseEntity<ApiResponse> handlingAuthorizationDeniedException(AuthorizationDeniedException exception){
        ErrorCode errorCode = ErrorCode.ACCESS_DENIED;
        return ResponseEntity.status(errorCode.getStatusCode())
                .body(ApiResponse.builder()
                        .code(errorCode.getCode())
                        .message(errorCode.getMessage())
                        .build());
    }

    // Xử lý lỗi Validation (khi dùng @Valid ở Controller).
    // Trả về một Map chứa chi tiết từng trường bị lỗi và thông báo tương ứng.
    @ExceptionHandler( value = MethodArgumentNotValidException.class)
    ResponseEntity<ApiResponse> handlingMethodArgumentNotValidException(MethodArgumentNotValidException exception){
        ErrorCode errorCode = ErrorCode.DATA_INVALID;
        Map<String, String> errors = new HashMap<>();
        exception.getBindingResult().getFieldErrors().forEach(fieldError -> {
                String enumKey = fieldError.getDefaultMessage();
                String fieldName = fieldError.getField();

                String message = ValidationError.from(enumKey)
                    .map(ValidationError::getMessage)
                    .orElseGet(() -> {
                            log.warn("Không tìm thấy ErrorCode tương ứng với enumKey '{}'", enumKey);
                            return errorCode.getMessage();
                    });

                errors.put(fieldName,message);
            }
        );

        return ResponseEntity.status(errorCode.getStatusCode())
                .body(ApiResponse.builder()
                        .code(errorCode.getCode())
                        .data(errors)
                        .message(errorCode.getMessage())
                        .build());
    }
}
