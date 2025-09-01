package com.ttt.CosmeticStore.service.impl;

import com.ttt.CosmeticStore.dto.response.FirebaseConfigResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class FirebaseConfigService {

    @Value("${firebase.api-key}")
    private String apiKey;

    @Value("${firebase.auth-domain}")
    private String authDomain;

    @Value("${firebase.project-id}")
    private String projectId;

    @Value("${firebase.storage-bucket}")
    private String storageBucket;

    @Value("${firebase.messaging-sender-id}")
    private String messagingSenderId;

    @Value("${firebase.app-id}")
    private String appId;

    @Value("${firebase.measurement-id}")
    private String measurementId;

    public FirebaseConfigResponse getConfig() {
        return new FirebaseConfigResponse(
                apiKey,
                authDomain,
                projectId,
                storageBucket,
                messagingSenderId,
                appId,
                measurementId
        );
    }
}