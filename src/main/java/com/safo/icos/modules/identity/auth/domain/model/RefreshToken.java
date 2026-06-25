package com.safo.icos.modules.identity.auth.domain.model;

import java.time.Instant;
import java.util.UUID;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RefreshToken {
  private final RefreshTokenId id;
  private final UUID membershipId;
  private final TokenValue token;
  private final String deviceInfo;
  private final Instant createdAt;
  private final Instant expiresAt;
  private final boolean revoked;
  private final Instant revokedAt;

  public static RefreshToken create(UUID membershipId, TokenValue token, String deviceInfo, Instant expiresAt) {
    return RefreshToken.builder()
            .id(RefreshTokenId.generate())
            .membershipId(membershipId)
            .token(token)
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
            .token(this.token)
            .deviceInfo(this.deviceInfo)
            .createdAt(this.createdAt)
            .expiresAt(this.expiresAt)
            .revoked(true)
            .revokedAt(Instant.now())
            .build();
  }
}
