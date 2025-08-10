package com.ttt.CosmeticStore.controller;

import com.ttt.CosmeticStore.dto.response.UserProfileResponse;
import com.ttt.CosmeticStore.entity.User;
import com.ttt.CosmeticStore.mapper.UserMapper;
import com.ttt.CosmeticStore.service.impl.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:3000")
public class ApiUserController {

    @Autowired
    private UserServiceImpl userService;

    @Autowired
    private UserMapper userMapper;

    @GetMapping("/secure/user/my-profile")
    public ResponseEntity<UserProfileResponse> getMyProfile() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        User user = userService.getUserByUsername(username);
        UserProfileResponse response = userMapper.toUserProfileResponse(user);

        return ResponseEntity.ok(response);
    }
}
