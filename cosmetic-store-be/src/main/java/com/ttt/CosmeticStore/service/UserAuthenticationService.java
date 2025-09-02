package com.ttt.CosmeticStore.service;

import com.ttt.CosmeticStore.entity.User;
import org.springframework.security.core.Authentication;

public interface UserAuthenticationService {

    Long getCurrentUserId(Authentication authentication);

    User getCurrentUser(Authentication authentication);

    String getCustomerName(Authentication authentication, String requestedName);

    boolean isValidUser(Authentication authentication);
}
