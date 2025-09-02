package com.ttt.CosmeticStore.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PolicyController {

    @GetMapping("/privacy")
    public String privacy() {
        return "Privacy Policy - CosmeticWeb Development Version";
    }

    @GetMapping("/terms")
    public String terms() {
        return "Terms of Service - CosmeticWeb Development Version";
    }

    @PostMapping("/delete-user-data")
    public ResponseEntity<?> deleteUserData(@RequestParam String userId) {
        // Log request for development
        System.out.println("Delete user data request for: " + userId);
        return ResponseEntity.ok("User data deletion processed");
    }
}
