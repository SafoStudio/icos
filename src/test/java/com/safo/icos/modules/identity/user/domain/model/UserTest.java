package com.safo.icos.modules.identity.user.domain.model;

import com.safo.icos.modules.identity.user.domain.model.enums.UserStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class UserTest {

  private User user;

  @BeforeEach
  void setUp() {
    user = User.create("John", "Doe", Email.of("john@example.com"), null);
  }

  @Test
  void shouldCreateActiveUser() {
    assertThat(user.getId()).isNotNull();
    assertThat(user.getFirstName()).isEqualTo("John");
    assertThat(user.getLastName()).isEqualTo("Doe");
    assertThat(user.getEmail().value()).isEqualTo("john@example.com");
    assertThat(user.getStatus()).isEqualTo(UserStatus.ACTIVE);
    assertThat(user.getCreatedAt()).isNotNull();
    assertThat(user.getUpdatedAt()).isNotNull();
  }

  @Test
  void shouldUpdateName() {
    user.updateName("Jane", "Smith");

    assertThat(user.getFirstName()).isEqualTo("Jane");
    assertThat(user.getLastName()).isEqualTo("Smith");
  }

  @Test
  void shouldActivateUser() {
    user.suspend();
    user.activate();

    assertThat(user.getStatus()).isEqualTo(UserStatus.ACTIVE);
  }

  @Test
  void shouldSuspendUser() {
    user.suspend();

    assertThat(user.getStatus()).isEqualTo(UserStatus.SUSPENDED);
  }

  @Test
  void shouldTerminateUser() {
    user.terminate();

    assertThat(user.getStatus()).isEqualTo(UserStatus.TERMINATED);
  }

  @Test
  void shouldTrackCreatedBy() {
    UUID creatorId = UUID.randomUUID();
    User createdUser = User.create("Jane", "Doe", Email.of("jane@example.com"), creatorId);

    assertThat(createdUser.getCreatedBy()).isEqualTo(creatorId);
  }
}