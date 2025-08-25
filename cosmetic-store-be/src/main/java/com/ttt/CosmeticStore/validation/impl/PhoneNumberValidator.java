package com.ttt.CosmeticStore.validation.impl;

import com.ttt.CosmeticStore.validation.ValidPhoneNumber;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PhoneNumberValidator implements ConstraintValidator<ValidPhoneNumber, String> {

    @Override
    public void initialize(ValidPhoneNumber constraintAnnotation) {
    }

    @Override
    public boolean isValid(String phoneNumber, ConstraintValidatorContext context) {
        if (phoneNumber == null || phoneNumber.trim().isEmpty()) {
            return false;
        }

        String cleanPhone = phoneNumber.replaceAll("[\\s-]", "");

        // Kiểm tra định dạng số điện thoại Việt Nam
        // 0xxx-xxx-xxx (10 số, bắt đầu bằng 0)
        // +84xxx-xxx-xxx (12 số, bắt đầu bằng +84)
        return cleanPhone.matches("^(0[3|5|7|8|9])+([0-9]{8})$") ||
                cleanPhone.matches("^(\\+84[3|5|7|8|9])+([0-9]{8})$");
    }
}