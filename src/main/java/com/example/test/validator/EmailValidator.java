package com.example.test.validator;

import com.example.test.validator.annotation.Gmail;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class EmailValidator implements ConstraintValidator<Gmail, String> {
    @Override
    public void initialize(Gmail constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(String email, ConstraintValidatorContext constraintValidatorContext) {
        if(email == null) return true;

        return email.endsWith("@gmail.com");
    }
}
