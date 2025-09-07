package com.ttt.CosmeticStore.service.impl;

import com.ttt.CosmeticStore.entity.User;
import com.ttt.CosmeticStore.service.UserAuthenticationService;
import com.ttt.CosmeticStore.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class UserAuthenticationServiceImpl implements UserAuthenticationService {

    @Autowired
    private UserService userService;

    @Override
    public Long getCurrentUserId(Authentication authentication) {
        User user = getCurrentUser(authentication);
        return user != null ? user.getId() : null;
    }

    @Override
    public User getCurrentUser(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }

        String authName = authentication.getName();
        if (!StringUtils.hasText(authName)) {
            return null;
        }

        try {
            Long userId = Long.parseLong(authName);
            return userService.getUserById(userId);
        } catch (NumberFormatException e) {
            try {
                return userService.findByUsername(authName);
            } catch (UsernameNotFoundException ex) {
                return null;
            }
        }
    }

    @Override
    public String getCustomerName(Authentication authentication, String requestedName) {
        if (StringUtils.hasText(requestedName)) {
            return requestedName;
        }

        User user = getCurrentUser(authentication);
        return user != null ? user.getUsername() : "Khách hàng";
    }

    @Override
    public boolean isValidUser(Authentication authentication) {
        return getCurrentUser(authentication) != null;
    }
}