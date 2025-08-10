package com.ttt.CosmeticStore.dto.response;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileResponse {
    private Long id;
    private String username;
    private String email;
    private String phone;
    private String gender;
    private String role;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
