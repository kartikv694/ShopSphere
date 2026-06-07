package com.Infosys.ecommerceApplication.config;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.cloudinary.Cloudinary;

@Configuration
public class CloudinaryConfig {

    @Autowired
    private EnvConfig envConfig;

    @Bean
    public Cloudinary cloudinary() {
        Map<String, String> config = new HashMap<>();

        config.put(
                "cloud_name",
                requireEnv("CLOUDINARY_CLOUD_NAME")
        );
        config.put(
                "api_key",
                requireEnv("CLOUDINARY_API_KEY")
        );
        config.put(
                "api_secret",
                requireEnv("CLOUDINARY_API_SECRET")
        );

        return new Cloudinary(config);
    }

    private String requireEnv(String key) {
        String value = envConfig.get(key);

        if (value == null || value.isBlank()) {
            throw new RuntimeException(
                    key + " is not configured"
            );
        }

        return value;
    }
}
