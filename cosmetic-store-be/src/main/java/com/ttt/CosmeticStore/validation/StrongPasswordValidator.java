package com.ttt.CosmeticStore.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.regex.Pattern;

public class StrongPasswordValidator implements ConstraintValidator<StrongPassword, String> {

    private int minLength;
    private boolean requireUppercase;
    private boolean requireLowercase;
    private boolean requireDigit;
    private boolean requireSpecialChar;

    // Regex patterns
    private static final Pattern UPPERCASE_PATTERN = Pattern.compile(".*[A-Z].*");
    private static final Pattern LOWERCASE_PATTERN = Pattern.compile(".*[a-z].*");
    private static final Pattern DIGIT_PATTERN = Pattern.compile(".*[0-9].*");
    private static final Pattern SPECIAL_CHAR_PATTERN = Pattern.compile(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?].*");

    @Override
    public void initialize(StrongPassword constraintAnnotation) {
        this.minLength = constraintAnnotation.minLength();
        this.requireUppercase = constraintAnnotation.requireUppercase();
        this.requireLowercase = constraintAnnotation.requireLowercase();
        this.requireDigit = constraintAnnotation.requireDigit();
        this.requireSpecialChar = constraintAnnotation.requireSpecialChar();
    }

    @Override
    public boolean isValid(String password, ConstraintValidatorContext context) {
        if (password == null || password.trim().isEmpty()) {
            return false;
        }

        context.disableDefaultConstraintViolation();

        boolean isValid = true;

        if (password.length() < minLength) {
            context.buildConstraintViolationWithTemplate(
                String.format("Mật khẩu phải có ít nhất %d ký tự", minLength))
                .addConstraintViolation();
            isValid = false;
        }

        if (requireUppercase && !UPPERCASE_PATTERN.matcher(password).matches()) {
            context.buildConstraintViolationWithTemplate(
                "Mật khẩu phải chứa ít nhất một chữ cái viết hoa (A-Z)")
                .addConstraintViolation();
            isValid = false;
        }

        if (requireLowercase && !LOWERCASE_PATTERN.matcher(password).matches()) {
            context.buildConstraintViolationWithTemplate(
                "Mật khẩu phải chứa ít nhất một chữ cái viết thường (a-z)")
                .addConstraintViolation();
            isValid = false;
        }

        if (requireDigit && !DIGIT_PATTERN.matcher(password).matches()) {
            context.buildConstraintViolationWithTemplate(
                "Mật khẩu phải chứa ít nhất một chữ số (0-9)")
                .addConstraintViolation();
            isValid = false;
        }

        if (requireSpecialChar && !SPECIAL_CHAR_PATTERN.matcher(password).matches()) {
            context.buildConstraintViolationWithTemplate(
                "Mật khẩu phải chứa ít nhất một ký tự đặc biệt (!@#$%^&*()_+-=[]{}|;':\",./<>?)")
                .addConstraintViolation();
            isValid = false;
        }

        return isValid;
    }
}
