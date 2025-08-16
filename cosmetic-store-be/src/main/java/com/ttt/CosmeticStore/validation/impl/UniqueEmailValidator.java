package com.ttt.CosmeticStore.validation.impl;

import com.ttt.CosmeticStore.repository.UserRepository;
import com.ttt.CosmeticStore.validation.UniqueEmail;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UniqueEmailValidator implements ConstraintValidator<UniqueEmail, String> {

    @Autowired
    private UserRepository userRepository;

    @Override
    public boolean isValid(String email, ConstraintValidatorContext context) {
        if (email == null || email.trim().isEmpty()) {
            return true; // Để @NotBlank và @Email xử lý
        }
        return !userRepository.existsByEmail(email);
    }
}
