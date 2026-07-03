package com.safo.icos.modules.identity.user.domain.model;

import lombok.Getter;

/**
 * Value object representing a user's first and last name.
 * Validates non-blank and maximum length of 100 characters each.
 */
@Getter
public class UserName {

  private final String firstName;
  private final String lastName;

  private UserName(String firstName, String lastName) {
    if (firstName == null || firstName.isBlank()) {
      throw new IllegalArgumentException("First name cannot be empty");
    }
    if (lastName == null || lastName.isBlank()) {
      throw new IllegalArgumentException("Last name cannot be empty");
    }
    if (firstName.length() > 100 || lastName.length() > 100) {
      throw new IllegalArgumentException("Name fields cannot exceed 100 characters");
    }
    this.firstName = firstName.trim();
    this.lastName = lastName.trim();
  }

  /**
   * Creates a {@code UserName} from raw strings.
   *
   * @param firstName the user's first name
   * @param lastName  the user's last name
   * @return a validated {@code UserName}
   * @throws IllegalArgumentException if either name is blank or exceeds 100 characters
   */
  public static UserName of(String firstName, String lastName) {
    return new UserName(firstName, lastName);
  }

  /** @return the full name as "{@code firstName lastName}" */
  public String getFullName() {
    return firstName + " " + lastName;
  }

}