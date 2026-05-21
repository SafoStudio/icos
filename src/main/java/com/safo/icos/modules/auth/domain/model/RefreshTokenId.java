package com.safo.icos.modules.auth.domain.model;

import java.util.Objects;
import java.util.UUID;
import org.jetbrains.annotations.NotNull;

public record RefreshTokenId(UUID value) {

  public RefreshTokenId {
    Objects.requireNonNull(value, "RefreshTokenId cannot be null");
  }

  public static RefreshTokenId generate() {
    return new RefreshTokenId(UUID.randomUUID());
  }

  public static RefreshTokenId fromString(String value) {
    return new RefreshTokenId(UUID.fromString(value));
  }

  @Override
  @NotNull
  public String toString() {
    return value.toString();
  }
}
