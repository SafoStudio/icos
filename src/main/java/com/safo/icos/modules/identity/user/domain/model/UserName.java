package com.safo.icos.modules.user.domain.model;

import lombok.Getter;

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

  public static UserName of(String firstName, String lastName) {
    return new UserName(firstName, lastName);
  }

  public String getFullName() {
    return firstName + " " + lastName;
  }
}
