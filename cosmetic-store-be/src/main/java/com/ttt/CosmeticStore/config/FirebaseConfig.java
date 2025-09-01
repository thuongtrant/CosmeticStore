package com.ttt.CosmeticStore.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import jakarta.validation.Validator;
@Configuration
@EnableConfigurationProperties
public class FirebaseConfig {

    @Value("${chat.max-message-length:1000}")
    private int maxMessageLength;

    @Value("${chat.max-rooms-per-admin:50}")
    private int maxRoomsPerAdmin;

    @Value("${chat.auto-close-inactive-days:30}")
    private int autoCloseInactiveDays;

    @Bean
    public Validator validator() {
        return new LocalValidatorFactoryBean();
    }

    // Getters
    public int getMaxMessageLength() { return maxMessageLength; }
    public int getMaxRoomsPerAdmin() { return maxRoomsPerAdmin; }
    public int getAutoCloseInactiveDays() { return autoCloseInactiveDays; }
}
