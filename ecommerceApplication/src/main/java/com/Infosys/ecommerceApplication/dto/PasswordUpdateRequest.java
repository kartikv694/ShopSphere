package com.Infosys.ecommerceApplication.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class PasswordUpdateRequest {

    @NotBlank(message = "Current password is required")
    private String currentPassword;

    @Size(min = 4, message = "Password must be at least 4 characters")
    private String newPassword;

    public String getCurrentPassword() {
        return currentPassword;
    }

    public String getNewPassword() {
        return newPassword;
    }
}
