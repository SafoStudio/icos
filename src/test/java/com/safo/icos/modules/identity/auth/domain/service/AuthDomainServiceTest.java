package com.safo.icos.modules.identity.auth.domain.service;

import com.safo.icos.modules.identity.auth.domain.exception.TokenExpiredException;
import com.safo.icos.modules.identity.auth.domain.exception.TokenNotFoundException;
import com.safo.icos.modules.identity.auth.domain.exception.TokenRevokedException;
import com.safo.icos.modules.identity.auth.domain.model.RefreshToken;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AuthDomainServiceTest {

  private final AuthDomainService service = new AuthDomainService();

  private static final UUID MEMBERSHIP_ID = UUID.randomUUID();
  private static final String DEVICE_INFO = "test-device";
  private static final Duration TTL = Duration.ofDays(30);

  @Test
  void shouldCreateRefreshTokenWithRawToken() {
    RefreshTokenCreationResult result = service.createRefreshToken(MEMBERSHIP_ID, DEVICE_INFO, TTL);

    assertThat(result.entity()).isNotNull();
    assertThat(result.entity().getMembershipId()).isEqualTo(MEMBERSHIP_ID);
    assertThat(result.entity().isValid()).isTrue();
    assertThat(result.entity().isRevoked()).isFalse();

    assertThat(result.rawToken()).isNotBlank();
    assertThat(result.entity().getToken().matchesRaw(result.rawToken())).isTrue();
  }

  @Test
  void shouldValidateValidToken() {
    RefreshTokenCreationResult result = service.createRefreshToken(MEMBERSHIP_ID, DEVICE_INFO, TTL);

    // No exception expected
    service.validateRefreshToken(result.entity());
  }

  @Test
  void shouldThrowWhenTokenNull() {
    assertThatThrownBy(() -> service.validateRefreshToken(null))
            .isInstanceOf(TokenNotFoundException.class)
            .hasMessageContaining("not found");
  }

  @Test
  void shouldThrowWhenTokenExpired() {
    Duration shortTtl = Duration.ofDays(-1);
    RefreshTokenCreationResult result = service.createRefreshToken(MEMBERSHIP_ID, DEVICE_INFO, shortTtl);

    RefreshToken token = result.entity();
    assertThatThrownBy(() -> validateToken(token))
            .isInstanceOf(TokenExpiredException.class)
            .hasMessageContaining("expired");
  }

  private void validateToken(RefreshToken token) {
    service.validateRefreshToken(token);
  }

  @Test
  void shouldThrowWhenTokenRevoked() {
    RefreshTokenCreationResult result = service.createRefreshToken(MEMBERSHIP_ID, DEVICE_INFO, TTL);
    RefreshToken revoked = result.entity().revoke();

    assertThatThrownBy(() -> service.validateRefreshToken(revoked))
            .isInstanceOf(TokenRevokedException.class)
            .hasMessageContaining("revoked");
  }

  @Test
  void shouldCreateTokensWithDifferentRawValues() {
    RefreshTokenCreationResult result1 = service.createRefreshToken(MEMBERSHIP_ID, DEVICE_INFO, TTL);
    RefreshTokenCreationResult result2 = service.createRefreshToken(MEMBERSHIP_ID, DEVICE_INFO, TTL);

    assertThat(result1.rawToken()).isNotEqualTo(result2.rawToken());
    assertThat(result1.entity().getToken()).isNotEqualTo(result2.entity().getToken());
  }
}
