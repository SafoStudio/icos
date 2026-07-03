package com.safo.icos.modules.identity.user.domain.service;

import com.safo.icos.modules.identity.user.domain.exception.EmailAlreadyExistsException;
import com.safo.icos.modules.identity.user.domain.exception.UserNotFoundException;
import com.safo.icos.modules.identity.user.domain.model.Email;
import com.safo.icos.modules.identity.user.domain.model.User;
import com.safo.icos.modules.identity.user.domain.model.UserId;
import com.safo.icos.modules.identity.user.domain.model.UserName;
import com.safo.icos.modules.identity.user.domain.repository.UserRepository;

import java.util.UUID;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Domain service for user lifecycle operations.
 * Enforces business rules such as email uniqueness before creation
 * and valid status transitions.
 */
@Service
@RequiredArgsConstructor
public class UserDomainService {

  private final UserRepository userRepository;

  /**
   * Creates a new user after checking that the email is not already in use.
   *
   * @param name      the user's first and last name
   * @param email     the user's validated email
   * @param createdBy ID of the user who initiated creation, or {@code null}
   * @return the newly created {@link User}
   * @throws EmailAlreadyExistsException if the email is already registered
   */
  public User createUser(UserName name, Email email, UUID createdBy) {
    if (userRepository.existsByEmail(email)) {
      throw new EmailAlreadyExistsException(email);
    }
    return User.create(name.getFirstName(), name.getLastName(), email, createdBy);
  }

  /**
   * Updates the status of a user with validation of the status value.
   *
   * @param userId the user's ID
   * @param status the target status: {@code ACTIVE}, {@code SUSPENDED}, or {@code TERMINATED}
   * @return the updated {@link User}
   * @throws UserNotFoundException     if the user does not exist
   * @throws IllegalArgumentException if the status string is invalid
   */
  public User updateStatus(UserId userId, String status) {
    User user = userRepository.findById(userId.getValue())
            .orElseThrow(() -> new UserNotFoundException(userId));

    switch (status.toUpperCase()) {
      case "ACTIVE"   -> user.activate();
      case "SUSPENDED" -> user.suspend();
      case "TERMINATED" -> user.terminate();
      default ->
              throw new IllegalArgumentException("Invalid status: " + status);
    }
    return user;
  }
}
