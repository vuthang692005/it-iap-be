package com.example.it_iap.validator;

import com.example.it_iap.validator.annotation.Gmail;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class GmailValidator implements ConstraintValidator<Gmail, String> {
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
