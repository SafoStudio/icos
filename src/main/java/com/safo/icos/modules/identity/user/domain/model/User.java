package com.safo.icos.modules.identity.user.domain.model;

import com.safo.icos.modules.identity.user.domain.model.enums.UserStatus;

import java.time.LocalDateTime;
import java.util.UUID;

import lombok.Builder;
import lombok.Getter;

/**
 * Domain entity representing a registered user.
 * Supports status transitions ({@link #activate}, {@link #suspend}, {@link #terminate})
 * and name updates.
 */
@Getter
@Builder
public class User {
  private final UUID id;
  private String firstName;
  private String lastName;
  private final Email email;
  private final UUID createdBy;
  private final LocalDateTime createdAt;
  private UserStatus status;
  private LocalDateTime updatedAt;

  /**
   * Factory method to create a new active user.
   *
   * @param firstName the user's first name
   * @param lastName  the user's last name
   * @param email     the user's validated email
   * @param createdBy ID of the user who created this account, or {@code null}
   * @return a newly created {@code User} in {@link UserStatus#ACTIVE} status
   */
  public static User create(String firstName, String lastName, Email email, UUID createdBy) {
    return User.builder()
            .id(UUID.randomUUID())
            .firstName(firstName)
            .lastName(lastName)
            .email(email)
            .status(UserStatus.ACTIVE)
            .createdBy(createdBy)
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build();
  }

  /** Updates the user's first and last name. */
  public void updateName(String firstName, String lastName) {
    this.firstName = firstName;
    this.lastName = lastName;
    this.updatedAt = LocalDateTime.now();
  }

  /** Sets status to {@link UserStatus#ACTIVE}. */
  public void activate() {
    this.status = UserStatus.ACTIVE;
    this.updatedAt = LocalDateTime.now();
  }

  /** Sets status to {@link UserStatus#SUSPENDED}. */
  public void suspend() {
    this.status = UserStatus.SUSPENDED;
    this.updatedAt = LocalDateTime.now();
  }

  /** Sets status to {@link UserStatus#TERMINATED}. */
  public void terminate() {
    this.status = UserStatus.TERMINATED;
    this.updatedAt = LocalDateTime.now();
  }
}
