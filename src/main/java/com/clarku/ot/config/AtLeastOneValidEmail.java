package com.clarku.ot.config;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

@Constraint(validatedBy = { EmailsListValidator.class })
@Target({ ElementType.FIELD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
public @interface AtLeastOneValidEmail {
    String message() default "At least one valid email should be required";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}

