package com.Infosys.ecommerceApplication.dto;

/**
 * Generic carrier for small per-user JSON blobs that need to sync across
 * devices/browsers (saved delivery location, recently viewed products).
 * The "value" is the raw JSON the frontend already builds — the backend
 * does not need to understand its internal shape, only persist and
 * return it for the logged-in user.
 */
public class UserPreferenceRequest {

    private String value;

    public UserPreferenceRequest() {
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
