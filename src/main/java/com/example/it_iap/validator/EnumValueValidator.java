package com.example.it_iap.validator;

import com.example.it_iap.validator.annotation.EnumValue;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.List;
import java.util.stream.Stream;

public class EnumValueValidator implements ConstraintValidator<EnumValue, String> {
    private List<String> acceptedValues;

    @Override
    public void initialize(EnumValue annotation) {
        // Lấy tất cả tên các phần tử trong Enum và chuyển thành List String
        acceptedValues = Stream.of(annotation.enumClass().getEnumConstants())
                .map(Enum::name)
                .toList();
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.trim().isEmpty()) return true;
        return acceptedValues.contains(value.toUpperCase());
    }
}
