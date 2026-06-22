package com.Infosys.ecommerceApplication.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import com.Infosys.ecommerceApplication.dto.AuthResponse;
import com.Infosys.ecommerceApplication.dto.LoginRequest;
import com.Infosys.ecommerceApplication.dto.RefreshTokenRequest;
import com.Infosys.ecommerceApplication.model.RefreshToken;
import com.Infosys.ecommerceApplication.model.User;
import com.Infosys.ecommerceApplication.repository.userRepository;
import com.Infosys.ecommerceApplication.service.RefreshTokenService;
import com.Infosys.ecommerceApplication.util.JwtUtil;

@RestController
@RequestMapping("/api/auth")
public class loginController {

    @Autowired
    private userRepository userRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private RefreshTokenService refreshTokenService;

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody LoginRequest request) {

        Optional<User> userOptional = userRepository.findByEmail(request.getEmail());

        //  User not found
        if (userOptional.isEmpty()) {
            return ResponseEntity.badRequest().body("User not found");
        }

        User user = userOptional.get();

        //  Password check
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        if (!encoder.matches(request.getPassword(), user.getPassword())) {
            return ResponseEntity.badRequest().body("Invalid password");
        }

        // Generate short-lived access token
        String token = jwtUtil.generateToken(user.getEmail());

        // Generate long-lived refresh token (30 days), persisted server-side
        String refreshToken = refreshTokenService.issueRefreshToken(user);

        //  Send token + refresh token + email + role
        return ResponseEntity.ok(
            new AuthResponse(
                token,
                refreshToken,
                jwtUtil.getAccessTokenValiditySeconds(),
                user.getEmail(),
                user.getRole().name()
            )
        );
    }

    /**
     * Issues a brand new access token (and a rotated refresh token) using a
     * still-valid refresh token. This is what lets a user stay logged in
     * past the short access-token lifetime without re-entering credentials,
     * including after closing the browser, as long as the refresh token
     * itself has not expired or been revoked.
     */
    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(@RequestBody RefreshTokenRequest request) {

        if (request.getRefreshToken() == null || request.getRefreshToken().isBlank()) {
            return ResponseEntity.badRequest().body("Refresh token is required");
        }

        try {
            RefreshToken validToken = refreshTokenService.validateRefreshToken(request.getRefreshToken());

            User user = validToken.getUser();

            String newAccessToken = jwtUtil.generateToken(user.getEmail());
            String newRefreshToken = refreshTokenService.rotateRefreshToken(validToken);

            return ResponseEntity.ok(
                new AuthResponse(
                    newAccessToken,
                    newRefreshToken,
                    jwtUtil.getAccessTokenValiditySeconds(),
                    user.getEmail(),
                    user.getRole().name()
                )
            );

        } catch (RuntimeException e) {
            return ResponseEntity.status(401).body(e.getMessage());
        }
    }

    /**
     * Manual logout: revokes the refresh token server-side so it can never
     * be used again, even if it had not yet expired.
     */
    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestBody RefreshTokenRequest request) {

        if (request.getRefreshToken() != null && !request.getRefreshToken().isBlank()) {
            refreshTokenService.revokeToken(request.getRefreshToken());
        }

        return ResponseEntity.ok("Logged out successfully");
    }
}
