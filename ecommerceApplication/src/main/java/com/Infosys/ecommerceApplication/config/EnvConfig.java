package com.Infosys.ecommerceApplication.config;

import org.springframework.context.annotation.Configuration;

import io.github.cdimascio.dotenv.Dotenv;
import jakarta.annotation.PostConstruct;

@Configuration
public class EnvConfig {

    @PostConstruct
    public void loadEnv() {

        Dotenv dotenv = Dotenv.configure()
                .ignoreIfMissing()
                .load();

        dotenv.entries().forEach(entry ->
                System.setProperty(
                        entry.getKey(),
                        entry.getValue()
                )
        );

        System.out.println(
                "SPRING_DATASOURCE_URL = "
                        + System.getProperty("SPRING_DATASOURCE_URL")
        );
    }

    public String get(String key) {
        return System.getProperty(key);
    }
}