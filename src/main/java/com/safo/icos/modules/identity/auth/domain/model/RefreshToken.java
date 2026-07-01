package com.safo.icos.modules.identity.auth.domain.model;

import java.time.Instant;
import java.util.UUID;
import lombok.Builder;
import lombok.Getter;

/**
 * Domain entity representing a refresh token.
 * Stores the token hash (never the raw value), associated membership,
 * device info for audit, and revocation status.
 * Supports token rotation via {@link #revoke()}.
 */
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

  /**
   * Factory method to create a new active refresh token.
   *
   * @param membershipId the membership this token belongs to
   * @param token        the token value object (hash)
   * @param deviceInfo   client device information
   * @param expiresAt    absolute expiration time
   * @return a newly created {@code RefreshToken}
   */
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


  /**
   * Checks whether this token is still usable: not revoked and not expired.
   *
   * @return {@code true} if the token can still be used
   */
  public boolean isValid() {
    return !revoked && Instant.now().isBefore(expiresAt);
  }

  /**
   * Creates a copy of this token marked as revoked.
   * The original instance is immutable and remains unchanged.
   *
   * @return a new {@code RefreshToken} with {@code revoked = true}
   */
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
