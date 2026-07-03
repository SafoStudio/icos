package com.safo.icos.modules.identity.user.domain.model;

import java.util.Objects;
import java.util.UUID;
import lombok.Getter;

/**
 * Strongly-typed identifier for a {@link User} entity.
 */
@Getter
public class UserId {

  private final UUID value;

  private UserId(UUID value) {
    if (value == null) {
      throw new IllegalArgumentException("UserId cannot be null");
    }
    this.value = value;
  }

  /**
   * Creates a {@code UserId} from an existing UUID.
   *
   * @param uuid the user's UUID
   * @return a new {@code UserId}
   */
  public static UserId fromUUID(UUID uuid) {
    return new UserId(uuid);
  }

  /**
   * Generates a new random {@code UserId}.
   *
   * @return a new unique identifier
   */
  public static UserId generate() {
    return new UserId(UUID.randomUUID());
  }

  /**
   * Creates a {@code UserId} from its string representation.
   *
   * @param value UUID string
   * @return the corresponding {@code UserId}
   * @throws IllegalArgumentException if the string is not a valid UUID
   */
  public static UserId fromString(String value) {
    return new UserId(UUID.fromString(value));
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    UserId userId = (UserId) o;
    return Objects.equals(value, userId.value);
  }

  @Override
  public int hashCode() {
    return Objects.hash(value);
  }

  @Override
  public String toString() {
    return value.toString();
  }
}