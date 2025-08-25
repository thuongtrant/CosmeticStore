package com.ttt.CosmeticStore.controller;

import com.ttt.CosmeticStore.config.JwtUtils;
import com.ttt.CosmeticStore.dto.response.JwtResponse;
import com.ttt.CosmeticStore.dto.response.UserProfileResponse;
import com.ttt.CosmeticStore.entity.User;
import com.ttt.CosmeticStore.mapper.UserMapper;
import com.ttt.CosmeticStore.service.impl.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/oauth2")
public class OAuth2Controller {

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private UserServiceImpl userService;

    @Autowired
    private UserMapper userMapper;

    @PostMapping("/verify-token")
    public ResponseEntity<?> verifyToken(@RequestParam String token) {
        try {
            if (jwtUtils.validateJwtToken(token)) {
                String username = jwtUtils.getUserNameFromJwtToken(token);
                User user = userService.getUserByUsername(username);

                UserProfileResponse userProfile = userMapper.toUserProfileResponse(user);

                return ResponseEntity.ok(new JwtResponse(
                    token,
                    user.getId(),
                    user.getUsername(),
                    user.getEmail(),
                    user.getRole().getName()
                ));
            } else {
                return ResponseEntity.badRequest()
                    .body(Map.of("error", "Invalid token"));
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", "Token verification failed", "message", e.getMessage()));
        }
    }

    @GetMapping("/user-info")
    public ResponseEntity<?> getCurrentUser() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.isAuthenticated()) {
                String username = authentication.getName();
                User user = userService.getUserByUsername(username);
                UserProfileResponse userProfile = userMapper.toUserProfileResponse(user);
                return ResponseEntity.ok(userProfile);
            } else {
                return ResponseEntity.badRequest()
                    .body(Map.of("error", "User not authenticated"));
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", "Failed to get user info", "message", e.getMessage()));
        }
    }
}
