package com.Infosys.ecommerceApplication.dto;

public class AuthResponse {

    private String token;
    private String refreshToken;
    private long expiresIn; // access token lifetime in seconds
    private String email;
    private String role;

    public AuthResponse(String token, String refreshToken, long expiresIn, String email, String role) {
        this.token = token;
        this.refreshToken = refreshToken;
        this.expiresIn = expiresIn;
        this.email = email;
        this.role = role;
    }

    // Kept for any existing callers that only deal with access token + identity
    public AuthResponse(String token, String email, String role) {
        this(token, null, 0, email, role);
    }

    public String getToken() {
        return token;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public long getExpiresIn() {
        return expiresIn;
    }

    public String getEmail() {
        return email;
    }

    public String getRole() {
        return role;
    }
}
