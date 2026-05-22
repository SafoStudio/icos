package com.safo.icos.modules.user.domain.model;

import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class User {
  private final UUID id;
  private final String firstName;
  private final String lastName;
  private final Email email;
  private final UUID createdBy;
  private final LocalDateTime createdAt;
  private UserStatus status;
  private LocalDateTime updatedAt;

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

  public void activate() {
    this.status = UserStatus.ACTIVE;
    this.updatedAt = LocalDateTime.now();
  }

  public void suspend() {
    this.status = UserStatus.SUSPENDED;
    this.updatedAt = LocalDateTime.now();
  }

  public void terminate() {
    this.status = UserStatus.TERMINATED;
    this.updatedAt = LocalDateTime.now();
  }

  public void changeEmail(Email newEmail) {
    // changeEmail
  }
}
