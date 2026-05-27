package com.example.it_iap.validator.annotation;

import com.example.it_iap.validator.GmailValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Constraint(
        validatedBy = {GmailValidator.class}
)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Gmail {
    String message() default "EMAIL_INVALID";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
