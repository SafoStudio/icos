package com.safo.icos.modules.auth.domain.model;

import java.security.SecureRandom;
import java.util.Base64;
import java.util.Objects;

public record TokenValue(String value) {

  private static final SecureRandom SECURE_RANDOM = new SecureRandom();

  public TokenValue {
    if (value == null || value.isBlank()) {
      throw new IllegalArgumentException("Token value cannot be empty");
    }
  }

  public static TokenValue generate() {
    byte[] randomBytes = new byte[32];
    SECURE_RANDOM.nextBytes(randomBytes);
    return new TokenValue(Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes));
  }

  public boolean equals(String rawToken) {
    return value.equals(rawToken);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    TokenValue that = (TokenValue) o;
    return Objects.equals(value, that.value);
  }
}
