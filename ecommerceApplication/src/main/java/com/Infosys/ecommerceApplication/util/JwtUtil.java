package com.Infosys.ecommerceApplication.util;

import java.security.Key;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Date;

import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtUtil {

    private final Key key = Keys.hmacShaKeyFor(
            "mysecretkeymysecretkeymysecretkey12".getBytes()
    );

    // ACCESS TOKEN: short lived, sent on every request as a Bearer token.
    // Kept short so a stolen access token has a small window of usefulness.
    private static final long ACCESS_TOKEN_VALIDITY_MS = 1000L * 60 * 15; // 15 minutes

    // REFRESH TOKEN: long lived, only ever sent to /api/auth/refresh and
    // /api/auth/logout. Used to silently mint new access tokens so the
    // user is not logged out just because 15 minutes passed.
    private static final long REFRESH_TOKEN_VALIDITY_MS = 1000L * 60 * 60 * 24 * 30L; // 30 days

    private final SecureRandom secureRandom = new SecureRandom();

    // ===================== ACCESS TOKEN =====================

    public String generateToken(String email) {
        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + ACCESS_TOKEN_VALIDITY_MS))
                .signWith(key)
                .compact();
    }

    public long getAccessTokenValiditySeconds() {
        return ACCESS_TOKEN_VALIDITY_MS / 1000;
    }

    public long getRefreshTokenValidityMillis() {
        return REFRESH_TOKEN_VALIDITY_MS;
    }

    public String extractUsername(String token) {
        return getClaims(token).getSubject();
    }

    public boolean validateToken(String token, String username) {
        try {
            return extractUsername(token).equals(username) && !isTokenExpired(token);
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    private boolean isTokenExpired(String token) {
        return getClaims(token).getExpiration().before(new Date());
    }

    private Claims getClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    // ===================== REFRESH TOKEN =====================

    /**
     * Generates a cryptographically random opaque refresh token (not a JWT).
     * Only the SHA-256 hash of this value is persisted server-side, so the
     * raw token returned here is the only copy that ever exists in plain text.
     */
    public String generateRawRefreshToken() {
        byte[] randomBytes = new byte[64];
        secureRandom.nextBytes(randomBytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);
    }

    public String hashToken(String rawToken) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(rawToken.getBytes());
            return Base64.getUrlEncoder().withoutPadding().encodeToString(hashBytes);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 algorithm not available", e);
        }
    }
}
