package com.kanoon_kendra.rbac.kanoon_kendra_rbac.repository;

import com.kanoon_kendra.rbac.kanoon_kendra_rbac.entity.RefreshToken;
import com.kanoon_kendra.rbac.kanoon_kendra_rbac.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByToken(String token);
    void deleteByUser(User user);
    void deleteByUserAndDeviceId(User user, String deviceId);
    long deleteByExpiresAtBefore(Instant cutoff);
}
