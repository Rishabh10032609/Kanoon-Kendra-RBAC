package com.kanoon_kendra.rbac.kanoon_kendra_rbac.scheduler;

import com.kanoon_kendra.rbac.kanoon_kendra_rbac.service.RefreshTokenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class TokenCleanupScheduler {

    private final RefreshTokenService refreshTokenService;

    // Run every day at 02:00 AM server time
    @Scheduled(cron = "0 0 2 * * *")
    public void cleanupExpiredTokens() {
        long removed = refreshTokenService.cleanupExpired();
        if (removed > 0) {
            log.info("CleanupExpiredTokens removed {} expired refresh tokens", removed);
        }
    }
}
