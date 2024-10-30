package com.clarku.ot.config;

import java.util.List;

import org.hibernate.validator.internal.constraintvalidators.bv.EmailValidator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class EmailsListValidator implements ConstraintValidator<AtLeastOneValidEmail, List<String>> {

    private final EmailValidator emailValidator = new EmailValidator();

    @Override
    public boolean isValid(List<String> emails, ConstraintValidatorContext context) {
        if (emails == null || emails.isEmpty()) {
            return false;  // List must contain at least one email
        }

        for (String email : emails) {
            if (email == null || !emailValidator.isValid(email, context)) {
                return false;  // Each entry must be a valid email
            }
        }

        return true;
    }
}
