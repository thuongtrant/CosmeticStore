package com.ttt.CosmeticStore.controller;

import com.ttt.CosmeticStore.dto.response.FirebaseConfigResponse;
import com.ttt.CosmeticStore.service.impl.FirebaseConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/firebase")
@CrossOrigin
public class FirebaseController {

    @Autowired
    private FirebaseConfigService firebaseConfigService;

    @GetMapping({"/firebase-config", "/config"})
    public ResponseEntity<FirebaseConfigResponse> getFirebaseConfig() {
        return ResponseEntity.ok(firebaseConfigService.getConfig());
    }
}