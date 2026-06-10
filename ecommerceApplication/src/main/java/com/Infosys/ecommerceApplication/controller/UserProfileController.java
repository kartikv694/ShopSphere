package com.Infosys.ecommerceApplication.controller;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.Infosys.ecommerceApplication.dto.PasswordUpdateRequest;
import com.Infosys.ecommerceApplication.dto.ProfileUpdateRequest;
import com.Infosys.ecommerceApplication.dto.UserProfileResponse;
import com.Infosys.ecommerceApplication.service.userService;
import com.Infosys.ecommerceApplication.util.JwtUtil;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/user")
public class UserProfileController {

    @Autowired
    private userService service;

    @Autowired
    private JwtUtil jwtUtil;

    @GetMapping("/profile")
    public ResponseEntity<UserProfileResponse> getProfile(
            Principal principal
    ) {
        return ResponseEntity.ok(
                service.getProfile(principal.getName())
        );
    }

    @PutMapping("/profile")
    public ResponseEntity<UserProfileResponse> updateProfile(
            @Valid @RequestBody ProfileUpdateRequest request,
            Principal principal
    ) {
        UserProfileResponse profile =
                service.updateProfile(
                        principal.getName(),
                        request
                );

        return ResponseEntity.ok(
                new UserProfileResponse(
                        profile.getId(),
                        profile.getName(),
                        profile.getEmail(),
                        profile.getRole(),
                        jwtUtil.generateToken(profile.getEmail())
                )
        );
    }

    @PutMapping("/password")
    public ResponseEntity<String> updatePassword(
            @Valid @RequestBody PasswordUpdateRequest request,
            Principal principal
    ) {
        service.updatePassword(
                principal.getName(),
                request
        );

        return ResponseEntity.ok(
                "Password updated successfully"
        );
    }
}
