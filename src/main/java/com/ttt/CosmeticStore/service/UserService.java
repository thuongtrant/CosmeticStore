package com.ttt.CosmeticStore.service;

import com.ttt.CosmeticStore.entity.User;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserService extends UserDetailsService {
    User getUserByUsername(String username);
    User findByUsername(String username);
}
