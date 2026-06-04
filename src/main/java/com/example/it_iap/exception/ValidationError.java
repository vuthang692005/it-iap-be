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
    OTP_INVALID("Mã OTP không hợp lệ"),
    PHONE_NUMBER_INVALID("Số điện thoại không hợp lệ"),
    TITLE_INVALID("Tiêu đề hồ sơ không hợp lệ"),
    TARGET_POSITION_INVALID("Vị trí mục tiêu không hợp lệ"),
    TARGET_LEVEL_INVALID("Cấp độ mục tiêu không hợp lệ"),
    SKILL_NAME_INVALID("Tên kỹ năng không được để trống"),
    YEARS_EXPERIENCE_INVALID("Số năm kinh nghiệm không được nhỏ hơn 0"),
    EXPERIENCE_POSITION_INVALID("Vị trí kinh nghiệm không được để trống"),
    PROJECT_NAME_INVALID("Tên dự án không được để trống"),
    ;
    private final String message;

    private static final Map<String, ValidationError> LOOKUP =
            Arrays.stream(values())
                    .collect(Collectors.toMap(Enum::name, Function.identity()));

    public static Optional<ValidationError> from(String key) {
        return Optional.ofNullable(LOOKUP.get(key));
    }
}
