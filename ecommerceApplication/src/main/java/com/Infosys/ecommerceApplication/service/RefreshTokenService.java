package com.Infosys.ecommerceApplication.service;

import java.time.Instant;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.Infosys.ecommerceApplication.model.RefreshToken;
import com.Infosys.ecommerceApplication.model.User;
import com.Infosys.ecommerceApplication.repository.RefreshTokenRepository;
import com.Infosys.ecommerceApplication.util.JwtUtil;

@Service
public class RefreshTokenService {

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Autowired
    private JwtUtil jwtUtil;

    /**
     * Issues a brand new refresh token for the given user and persists only
     * its hash. Returns the raw token, which the caller must hand back to the
     * client exactly once.
     */
    public String issueRefreshToken(User user) {
        String rawToken = jwtUtil.generateRawRefreshToken();
        String hashed = jwtUtil.hashToken(rawToken);

        Instant expiry = Instant.now().plusMillis(jwtUtil.getRefreshTokenValidityMillis());

        RefreshToken refreshToken = new RefreshToken(hashed, user, expiry);
        refreshTokenRepository.save(refreshToken);

        return rawToken;
    }

    /**
     * Validates a raw refresh token presented by the client. Throws if the
     * token is unknown, revoked, or expired.
     */
    public RefreshToken validateRefreshToken(String rawToken) {
        String hashed = jwtUtil.hashToken(rawToken);

        RefreshToken stored = refreshTokenRepository.findByTokenHash(hashed)
                .orElseThrow(() -> new RuntimeException("Invalid refresh token"));

        if (stored.isRevoked()) {
            throw new RuntimeException("Refresh token has been revoked");
        }

        if (stored.isExpired()) {
            throw new RuntimeException("Refresh token has expired");
        }

        return stored;
    }

    /**
     * Rotates a refresh token: the old one is revoked and a new one is
     * issued for the same user. Rotation limits the damage a leaked refresh
     * token can do, since each one is only usable a single time.
     */
    public String rotateRefreshToken(RefreshToken oldToken) {
        oldToken.setRevoked(true);
        refreshTokenRepository.save(oldToken);

        return issueRefreshToken(oldToken.getUser());
    }

    public void revokeToken(String rawToken) {
        String hashed = jwtUtil.hashToken(rawToken);

        refreshTokenRepository.findByTokenHash(hashed)
                .ifPresent(token -> {
                    token.setRevoked(true);
                    refreshTokenRepository.save(token);
                });
    }

    public void revokeAllForUser(User user) {
        refreshTokenRepository.revokeAllForUser(user);
    }
}
