package com.safo.icos.modules.identity.user.domain.model;

import java.util.Objects;
import java.util.regex.Pattern;

/**
 * Value object representing a validated email address.
 * Normalised to lowercase on creation.
 */
public record Email(String value) {

  private static final Pattern EMAIL_PATTERN =
          Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");

  public Email {
    if (value == null || value.isBlank()) {
      throw new IllegalArgumentException("Email cannot be empty");
    }
    if (!EMAIL_PATTERN.matcher(value).matches()) {
      throw new IllegalArgumentException("Invalid email format: " + value);
    }
    value = value.toLowerCase();
  }

  /**
   * Creates an {@code Email} from a raw string.
   *
   * @param value the raw email string
   * @return a validated, lowercased {@code Email}
   * @throws IllegalArgumentException if the value is empty or invalid
   */
  public static Email of(String value) {
    return new Email(value);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Email email = (Email) o;
    return Objects.equals(value, email.value);
  }
}