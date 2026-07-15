package com.safo.icos.security.jwt;

import com.safo.icos.modules.identity.auth.api.dto.AuthTokenDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class JwtServiceTest {

  private final JwtService jwtService = new JwtService();

  private static final UUID USER_ID = UUID.randomUUID();
  private static final UUID TENANT_ID = UUID.randomUUID();

  @BeforeEach
  void setUp() {
    ReflectionTestUtils.setField(jwtService, "secret", "my-test-secret-key-that-is-long-enough-for-hmac");
    ReflectionTestUtils.setField(jwtService, "accessTokenTtlSeconds", 900);
    jwtService.init();
  }

  @Test
  void shouldGenerateAccessToken() {
    AuthTokenDto result = jwtService.generateAccessToken(USER_ID, TENANT_ID);

    assertThat(result.getAccessToken()).isNotBlank();
    assertThat(result.getTokenType()).isEqualTo("Bearer");
    assertThat(result.getExpiresIn()).isEqualTo(900);
  }

  @Test
  void shouldValidateValidToken() {
    AuthTokenDto tokenDto = jwtService.generateAccessToken(USER_ID, TENANT_ID);

    boolean valid = jwtService.validateToken(tokenDto.getAccessToken());

    assertThat(valid).isTrue();
  }

  @Test
  void shouldRejectTamperedToken() {
    AuthTokenDto tokenDto = jwtService.generateAccessToken(USER_ID, TENANT_ID);
    String tamperedToken = tokenDto.getAccessToken() + "x";

    boolean valid = jwtService.validateToken(tamperedToken);

    assertThat(valid).isFalse();
  }

  @Test
  void shouldRejectInvalidToken() {
    boolean valid = jwtService.validateToken("not.a.jwt.token");

    assertThat(valid).isFalse();
  }

  @Test
  void shouldRejectEmptyToken() {
    boolean valid = jwtService.validateToken("");

    assertThat(valid).isFalse();
  }

  @Test
  void shouldExtractUserId() {
    AuthTokenDto tokenDto = jwtService.generateAccessToken(USER_ID, TENANT_ID);

    String userId = jwtService.extractUserId(tokenDto.getAccessToken());

    assertThat(userId).isEqualTo(USER_ID.toString());
  }

  @Test
  void shouldExtractTenantId() {
    AuthTokenDto tokenDto = jwtService.generateAccessToken(USER_ID, TENANT_ID);

    String tenantId = jwtService.extractTenantId(tokenDto.getAccessToken());

    assertThat(tenantId).isEqualTo(TENANT_ID.toString());
  }
}
