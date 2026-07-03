package com.safo.icos.modules.identity.auth.domain.model;

import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class RefreshTokenTest {

  private static final UUID MEMBERSHIP_ID = UUID.randomUUID();
  private static final String DEVICE_INFO = "test-device";

  @Test
  void shouldCreateActiveToken() {
    TokenValue token = TokenValue.fromRaw(TokenValue.generateRaw());
    Instant expiresAt = Instant.now().plus(Duration.ofDays(30));

    RefreshToken refreshToken = RefreshToken.create(MEMBERSHIP_ID, token, DEVICE_INFO, expiresAt);

    assertThat(refreshToken.getId()).isNotNull();
    assertThat(refreshToken.getMembershipId()).isEqualTo(MEMBERSHIP_ID);
    assertThat(refreshToken.getToken()).isEqualTo(token);
    assertThat(refreshToken.getDeviceInfo()).isEqualTo(DEVICE_INFO);
    assertThat(refreshToken.getCreatedAt()).isNotNull();
    assertThat(refreshToken.getExpiresAt()).isEqualTo(expiresAt);
    assertThat(refreshToken.isRevoked()).isFalse();
    assertThat(refreshToken.isValid()).isTrue();
  }

  @Test
  void shouldBeInvalidWhenExpired() {
    TokenValue token = TokenValue.fromRaw(TokenValue.generateRaw());
    Instant expiresAt = Instant.now().minus(Duration.ofDays(1));

    RefreshToken refreshToken = RefreshToken.create(MEMBERSHIP_ID, token, DEVICE_INFO, expiresAt);

    assertThat(refreshToken.isValid()).isFalse();
  }

  @Test
  void shouldRevokeImmutably() {
    TokenValue token = TokenValue.fromRaw(TokenValue.generateRaw());
    Instant expiresAt = Instant.now().plus(Duration.ofDays(30));
    RefreshToken original = RefreshToken.create(MEMBERSHIP_ID, token, DEVICE_INFO, expiresAt);

    RefreshToken revoked = original.revoke();

    // Original unchanged
    assertThat(original.isRevoked()).isFalse();
    assertThat(original.isValid()).isTrue();

    // Revoked copy
    assertThat(revoked.isRevoked()).isTrue();
    assertThat(revoked.isValid()).isFalse();
    assertThat(revoked.getRevokedAt()).isNotNull();

    // Same identity
    assertThat(revoked.getId()).isEqualTo(original.getId());
    assertThat(revoked.getToken()).isEqualTo(original.getToken());
    assertThat(revoked.getMembershipId()).isEqualTo(original.getMembershipId());
  }

  @Test
  void shouldBeInvalidWhenRevoked() {
    TokenValue token = TokenValue.fromRaw(TokenValue.generateRaw());
    Instant expiresAt = Instant.now().plus(Duration.ofDays(30));
    RefreshToken refreshToken = RefreshToken.create(MEMBERSHIP_ID, token, DEVICE_INFO, expiresAt);

    RefreshToken revoked = refreshToken.revoke();

    assertThat(revoked.isValid()).isFalse();
  }

  @Test
  void shouldBeInvalidWhenExpiredAndRevoked() {
    TokenValue token = TokenValue.fromRaw(TokenValue.generateRaw());
    Instant expiresAt = Instant.now().minus(Duration.ofDays(1));
    RefreshToken refreshToken = RefreshToken.create(MEMBERSHIP_ID, token, DEVICE_INFO, expiresAt);

    RefreshToken revoked = refreshToken.revoke();

    assertThat(revoked.isValid()).isFalse();
  }
}