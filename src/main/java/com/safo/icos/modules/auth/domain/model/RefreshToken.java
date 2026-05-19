// modules/auth/domain/model/RefreshToken.java
package com.safo.icos.modules.auth.domain.model;

import lombok.Builder;
import lombok.Getter;
import java.time.Instant;
import java.util.UUID;

@Getter
@Builder
public class RefreshToken {
    private final UUID id;
    private final UUID membershipId;
    private final String tokenHash;
    private final String deviceInfo;
    private final Instant createdAt;
    private final Instant expiresAt;
    private final boolean revoked;
    private final Instant revokedAt;

    public static RefreshToken create(UUID membershipId, String tokenHash, String deviceInfo, Instant expiresAt) {
        return RefreshToken.builder()
                .id(UUID.randomUUID())
                .membershipId(membershipId)
                .tokenHash(tokenHash)
                .deviceInfo(deviceInfo)
                .createdAt(Instant.now())
                .expiresAt(expiresAt)
                .revoked(false)
                .revokedAt(null)
                .build();
    }

    public boolean isValid() {
        return !revoked && Instant.now().isBefore(expiresAt);
    }

    public RefreshToken revoke() {
        return RefreshToken.builder()
                .id(this.id)
                .membershipId(this.membershipId)
                .tokenHash(this.tokenHash)
                .deviceInfo(this.deviceInfo)
                .createdAt(this.createdAt)
                .expiresAt(this.expiresAt)
                .revoked(true)
                .revokedAt(Instant.now())
                .build();
    }
}