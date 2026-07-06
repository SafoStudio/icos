package com.safo.icos.modules.identity.user.domain.service;

import com.safo.icos.modules.identity.user.domain.exception.EmailAlreadyExistsException;
import com.safo.icos.modules.identity.user.domain.exception.UserNotFoundException;
import com.safo.icos.modules.identity.user.domain.model.Email;
import com.safo.icos.modules.identity.user.domain.model.User;
import com.safo.icos.modules.identity.user.domain.model.UserId;
import com.safo.icos.modules.identity.user.domain.model.UserName;
import com.safo.icos.modules.identity.user.domain.model.enums.UserStatus;
import com.safo.icos.modules.identity.user.domain.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserDomainServiceTest {

  @Mock private UserRepository userRepository;

  @InjectMocks
  private UserDomainService userDomainService;

  @Test
  void shouldCreateUserSuccessfully() {
    UserName name = UserName.of("John", "Doe");
    Email email = Email.of("john@example.com");

    when(userRepository.existsByEmail(email)).thenReturn(false);

    User user = userDomainService.createUser(name, email, null);

    assertThat(user.getFirstName()).isEqualTo("John");
    assertThat(user.getLastName()).isEqualTo("Doe");
    assertThat(user.getEmail()).isEqualTo(email);
    assertThat(user.getStatus()).isEqualTo(UserStatus.ACTIVE);
  }

  @Test
  void shouldThrowWhenEmailAlreadyExistsOnCreate() {
    UserName name = UserName.of("John", "Doe");
    Email email = Email.of("john@example.com");

    when(userRepository.existsByEmail(email)).thenReturn(true);

    assertThatThrownBy(() -> userDomainService.createUser(name, email, null))
            .isInstanceOf(EmailAlreadyExistsException.class)
            .hasMessageContaining("already exists");
  }

  @Test
  void shouldUpdateStatusToActive() {
    User user = User.create("John", "Doe", Email.of("john@example.com"), null);
    UserId userId = UserId.fromUUID(user.getId());

    when(userRepository.findById(userId.getValue())).thenReturn(Optional.of(user));

    User updated = userDomainService.updateStatus(userId, "ACTIVE");

    assertThat(updated.getStatus()).isEqualTo(UserStatus.ACTIVE);
  }

  @Test
  void shouldUpdateStatusToSuspended() {
    User user = User.create("John", "Doe", Email.of("john@example.com"), null);
    UserId userId = UserId.fromUUID(user.getId());

    when(userRepository.findById(userId.getValue())).thenReturn(Optional.of(user));

    User updated = userDomainService.updateStatus(userId, "SUSPENDED");

    assertThat(updated.getStatus()).isEqualTo(UserStatus.SUSPENDED);
  }

  @Test
  void shouldUpdateStatusToTerminated() {
    User user = User.create("John", "Doe", Email.of("john@example.com"), null);
    UserId userId = UserId.fromUUID(user.getId());

    when(userRepository.findById(userId.getValue())).thenReturn(Optional.of(user));

    User updated = userDomainService.updateStatus(userId, "TERMINATED");

    assertThat(updated.getStatus()).isEqualTo(UserStatus.TERMINATED);
  }

  @Test
  void shouldThrowWhenUserNotFoundOnUpdateStatus() {
    UserId userId = UserId.generate();

    when(userRepository.findById(userId.getValue())).thenReturn(Optional.empty());

    assertThatThrownBy(() -> userDomainService.updateStatus(userId, "ACTIVE"))
            .isInstanceOf(UserNotFoundException.class)
            .hasMessageContaining("not found");
  }

  @Test
  void shouldThrowForInvalidStatus() {
    User user = User.create("John", "Doe", Email.of("john@example.com"), null);
    UserId userId = UserId.fromUUID(user.getId());

    when(userRepository.findById(userId.getValue())).thenReturn(Optional.of(user));

    assertThatThrownBy(() -> userDomainService.updateStatus(userId, "INVALID"))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Invalid status");
  }
}