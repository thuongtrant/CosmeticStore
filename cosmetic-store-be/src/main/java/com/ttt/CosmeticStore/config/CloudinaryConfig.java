package com.ttt.CosmeticStore.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.cloudinary.Cloudinary;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class CloudinaryConfig {
    @Bean
    public Cloudinary getCloudinary(){
        Map config = new HashMap();
        config.put("cloud_name", "dxedz1icn");
        config.put("api_key", "652251899769451");
        config.put("api_secret", "tJpmpfsHYxmjehBFFObwHPHGkP8");
        config.put("secure", true);
        return new Cloudinary(config);
    }
}
