package com.safo.icos.modules.identity.user.domain.model;

import com.safo.icos.modules.identity.user.domain.model.enums.UserStatus;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Builder;
import lombok.Getter;

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

  public void updateName(String firstName, String lastName) {
    this.firstName = firstName;
    this.lastName = lastName;
    this.updatedAt = LocalDateTime.now();
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
    // Logic for changing email
  }
}
