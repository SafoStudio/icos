package com.safo.icos.modules.user.domain.model;

import java.util.Objects;
import java.util.regex.Pattern;

public record Email(String value) {
  private static final Pattern EMAIL_PATTERN =
          Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");

  public Email(String value) {
    if (value == null || value.isBlank()) {
      throw new IllegalArgumentException("Email cannot be empty");
    }
    if (!EMAIL_PATTERN.matcher(value).matches()) {
      throw new IllegalArgumentException("Invalid email format: " + value);
    }
    this.value = value.toLowerCase();
  }

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