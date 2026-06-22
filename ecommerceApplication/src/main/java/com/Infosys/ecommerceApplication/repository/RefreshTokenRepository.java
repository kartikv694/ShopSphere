package com.Infosys.ecommerceApplication.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.Infosys.ecommerceApplication.model.RefreshToken;
import com.Infosys.ecommerceApplication.model.User;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    Optional<RefreshToken> findByTokenHash(String tokenHash);

    @Modifying
    @Transactional
    @Query("update RefreshToken r set r.revoked = true where r.user = :user and r.revoked = false")
    void revokeAllForUser(User user);

    @Modifying
    @Transactional
    void deleteByExpiryDateBefore(java.time.Instant cutoff);
}
