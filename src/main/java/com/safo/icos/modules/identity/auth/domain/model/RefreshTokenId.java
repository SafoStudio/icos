package com.safo.icos.modules.identity.auth.domain.model;

import java.util.Objects;
import java.util.UUID;
import org.jspecify.annotations.NonNull;

/**
 * Strongly-typed identifier for a {@link RefreshToken} entity.
 */
public record RefreshTokenId(UUID value) {

  public RefreshTokenId {
    Objects.requireNonNull(value, "RefreshTokenId cannot be null");
  }

  /**
   * Generates a new random unique identifier.
   *
   * @return a new {@code RefreshTokenId} with a random UUID
   */
  public static RefreshTokenId generate() {
    return new RefreshTokenId(UUID.randomUUID());
  }

  /**
   * Creates a {@code RefreshTokenId} from its string representation.
   *
   * @param value UUID string
   * @return the corresponding {@code RefreshTokenId}
   * @throws IllegalArgumentException if the string is not a valid UUID
   */
  public static RefreshTokenId fromString(String value) {
    return new RefreshTokenId(UUID.fromString(value));
  }

  @Override
  public @NonNull String toString() {
    return value.toString();
  }
}
