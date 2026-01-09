package com.example.test.validator.annotation;

import com.example.test.validator.EmailValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Constraint(
        validatedBy = {EmailValidator.class}
)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Gmail {
    String message() default "EMAIL_INVALID";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
