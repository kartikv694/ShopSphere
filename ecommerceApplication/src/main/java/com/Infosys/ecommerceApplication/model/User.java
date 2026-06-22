package com.Infosys.ecommerceApplication.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import com.fasterxml.jackson.annotation.JsonProperty;

@Entity
@Table(name="users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message="Name is required")
    private String name;

    @Email(message="Invalid email format")
    @NotBlank(message="Email required")
    private String email;

    @Size(min=4,message="Password must be at least 4 characters")
    private String password;

    @Enumerated(EnumType.STRING)
    private Role role;

    // Cross-device sync of the customer's delivery address picker. Stored
    // as a raw JSON string (same shape the frontend already builds:
    // fullName/fullAddress/address/city/state/pincode/lat/lng) so it can
    // change shape on the frontend without needing a migration here.
    @Column(columnDefinition = "TEXT")
    private String savedLocation;

    // Cross-device sync of "Recently Viewed" products, capped at the last 6
    // (same cap the frontend already enforced when this lived only in
    // localStorage). Stored as a raw JSON array string of product objects.
    @Column(columnDefinition = "TEXT")
    private String recentlyViewed;

    // getters setters

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    // WRITE_ONLY: accepted in requests, never sent in responses
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    public String getPassword() {
        return password;
    }

    public Role getRole() {
        return role;
    }

    public String getSavedLocation() {
        return savedLocation;
    }

    public void setSavedLocation(String savedLocation) {
        this.savedLocation = savedLocation;
    }

    public String getRecentlyViewed() {
        return recentlyViewed;
    }

    public void setRecentlyViewed(String recentlyViewed) {
        this.recentlyViewed = recentlyViewed;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setRole(Role role) {
        this.role = role;
    }
}
