package com.safo.icos.modules.identity.user.domain.model;

import java.util.Objects;
import java.util.UUID;
import lombok.Getter;

@Getter
public class UserId {
  private final UUID value;

  private UserId(UUID value) {
    if (value == null) {
      throw new IllegalArgumentException("UserId cannot be null");
    }
    this.value = value;
  }

  public static UserId fromUUID(UUID uuid) {
    return new UserId(uuid);
  }

  public static UserId generate() {
    return new UserId(UUID.randomUUID());
  }

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
