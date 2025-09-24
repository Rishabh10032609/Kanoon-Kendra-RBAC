    package com.kanoon_kendra.rbac.kanoon_kendra_rbac.service;

import com.kanoon_kendra.rbac.kanoon_kendra_rbac.entity.RefreshToken;
import com.kanoon_kendra.rbac.kanoon_kendra_rbac.entity.User;
import com.kanoon_kendra.rbac.kanoon_kendra_rbac.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;

    @Value("${app.jwt.refresh-expiration-ms:604800000}") // default 7 days
    private long refreshExpirationMs;

    public RefreshToken issue(User user, String deviceId) {
        // Keep at most one active token per user+device
        if (deviceId != null && !deviceId.isBlank()) {
            refreshTokenRepository.deleteByUserAndDeviceId(user, deviceId);
        }
        RefreshToken token = RefreshToken.builder()
                .user(user)
                .deviceId(deviceId)
                .token(UUID.randomUUID().toString())
                .expiresAt(Instant.now().plusMillis(refreshExpirationMs))
                .revoked(false)
                .build();
        return refreshTokenRepository.save(token);
    }

    public boolean validate(String tokenStr) {
        return refreshTokenRepository.findByToken(tokenStr)
                .filter(t -> !t.isRevoked())
                .filter(t -> t.getExpiresAt().isAfter(Instant.now()))
                .isPresent();
    }

    public User getUser(String tokenStr) {
        return refreshTokenRepository.findByToken(tokenStr)
                .map(RefreshToken::getUser)
                .orElse(null);
    }

    public void revoke(String tokenStr) {
        refreshTokenRepository.findByToken(tokenStr).ifPresent(t -> {
            t.setRevoked(true);
            refreshTokenRepository.save(t);
        });
    }

    public void revokeAllFor(User user) {
        refreshTokenRepository.deleteByUser(user);
    }

    public void revokeFor(User user, String deviceId) {
        refreshTokenRepository.deleteByUserAndDeviceId(user, deviceId);
    }

    public long cleanupExpired() {
        return refreshTokenRepository.deleteByExpiresAtBefore(Instant.now());
    }
}
