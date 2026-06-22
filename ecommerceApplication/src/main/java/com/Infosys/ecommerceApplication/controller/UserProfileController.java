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
import com.Infosys.ecommerceApplication.dto.UserPreferenceRequest;
import com.Infosys.ecommerceApplication.dto.UserPreferenceResponse;
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

    /**
     * Cross-device sync for the saved delivery address — same data that
     * previously only lived in localStorage["selectedLocation"] in
     * whichever browser the user last used.
     */
    @GetMapping("/saved-location")
    public ResponseEntity<UserPreferenceResponse> getSavedLocation(
            Principal principal
    ) {
        return ResponseEntity.ok(
                new UserPreferenceResponse(
                        service.getSavedLocation(principal.getName())
                )
        );
    }

    @PutMapping("/saved-location")
    public ResponseEntity<String> updateSavedLocation(
            @RequestBody UserPreferenceRequest request,
            Principal principal
    ) {
        service.updateSavedLocation(
                principal.getName(),
                request.getValue()
        );

        return ResponseEntity.ok("Saved location updated");
    }

    /**
     * Cross-device sync for "Recently Viewed" products — same data that
     * previously only lived in localStorage["recentlyViewed"] in whichever
     * browser the user last used.
     */
    @GetMapping("/recently-viewed")
    public ResponseEntity<UserPreferenceResponse> getRecentlyViewed(
            Principal principal
    ) {
        return ResponseEntity.ok(
                new UserPreferenceResponse(
                        service.getRecentlyViewed(principal.getName())
                )
        );
    }

    @PutMapping("/recently-viewed")
    public ResponseEntity<String> updateRecentlyViewed(
            @RequestBody UserPreferenceRequest request,
            Principal principal
    ) {
        service.updateRecentlyViewed(
                principal.getName(),
                request.getValue()
        );

        return ResponseEntity.ok("Recently viewed updated");
    }
}
