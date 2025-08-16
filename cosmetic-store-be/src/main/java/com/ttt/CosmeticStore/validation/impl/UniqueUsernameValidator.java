package com.ttt.CosmeticStore.validation.impl;

import com.ttt.CosmeticStore.repository.UserRepository;
import com.ttt.CosmeticStore.validation.UniqueUsername;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UniqueUsernameValidator implements ConstraintValidator<UniqueUsername, String> {

    @Autowired
    private UserRepository userRepository;

    @Override
    public boolean isValid(String username, ConstraintValidatorContext context) {
        if (username == null || username.trim().isEmpty()) {
            return true; // Để @NotBlank xử lý
        }
        return !userRepository.existsByUsername(username);
    }
}