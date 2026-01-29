package com.example.test.exception;

import com.example.test.dto.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;

@Slf4j(topic = "GlobalExceptionHandler")
@ControllerAdvice
public class GlobalExceptionHandler {
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

    @ExceptionHandler(value = ParseException.class)
    ResponseEntity<ApiResponse> handlingParseException(ParseException exception){
        ErrorCode errorCode = ErrorCode.AUTHENTICATION_FAILED;
        return ResponseEntity.status(errorCode.getStatusCode())
                .body(ApiResponse.builder()
                        .code(errorCode.getCode())
                        .message(errorCode.getMessage())
                        .build());
    }

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

    @ExceptionHandler(value = AppException.class)
    ResponseEntity<ApiResponse> handlingAppException(AppException exception){
        ErrorCode errorCode = exception.getErrorCode();
        return ResponseEntity.status(errorCode.getStatusCode())
                .body(ApiResponse.builder()
                        .code(errorCode.getCode())
                        .message(errorCode.getMessage())
                        .build());
    }

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
