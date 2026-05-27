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
    PASSWORD_INVALID("Mật khấu không hợp lệ"),
    EMAIL_INVALID("Email không hợp lệ"),
    FULL_NAME_INVALID("Họ tên không hợp lệ"),
    USER_ID_INVALID("Id người dùng không hợp lệ"),
    OTP_INVALID("Mã OTP không hợp lệ")
    ;
    private final String message;

    private static final Map<String, ValidationError> LOOKUP =
            Arrays.stream(values())
                    .collect(Collectors.toMap(Enum::name, Function.identity()));

    public static Optional<ValidationError> from(String key) {
        return Optional.ofNullable(LOOKUP.get(key));
    }
}
