package com.Infosys.ecommerceApplication.config;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

@Component
public class EnvConfig {

    private final Map<String, String> fileValues = new HashMap<>();

    public EnvConfig() {
        loadEnvFile(Path.of(".env"));
    }

    public String get(String key) {
        String envValue = System.getenv(key);

        if (envValue != null && !envValue.isBlank()) {
            return envValue;
        }

        return fileValues.getOrDefault(key, "");
    }

    private void loadEnvFile(Path path) {
        if (!Files.exists(path)) {
            return;
        }

        try {
            List<String> lines = Files.readAllLines(path);

            for (String line : lines) {
                String trimmedLine = line.trim();

                if (
                        trimmedLine.isBlank() ||
                        trimmedLine.startsWith("#") ||
                        !trimmedLine.contains("=")
                ) {
                    continue;
                }

                String[] parts = trimmedLine.split("=", 2);
                fileValues.put(parts[0].trim(), parts[1].trim());
            }
        } catch (IOException ignored) {
            fileValues.clear();
        }
    }
}
