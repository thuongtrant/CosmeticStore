package com.ttt.CosmeticStore.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FirebaseConfigResponse {
    private String apiKey;
    private String authDomain;
    private String projectId;
    private String storageBucket;
    private String messagingSenderId;
    private String appId;
    private String measurementId;
}