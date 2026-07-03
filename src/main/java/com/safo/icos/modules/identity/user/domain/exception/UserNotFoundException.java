package com.safo.icos.modules.identity.user.domain.exception;

import com.safo.icos.common.exception.NotFoundException;
import com.safo.icos.modules.identity.user.domain.model.Email;
import com.safo.icos.modules.identity.user.domain.model.UserId;

/**
 * Thrown when a user cannot be found by ID or email.
 */
public class UserNotFoundException extends NotFoundException {

  public UserNotFoundException(String message) {
    super(message);
  }

  public UserNotFoundException(UserId userId) {
    super("User not found with id: " + userId);
  }

  public UserNotFoundException(Email email) {
    super("User not found with email: " + email);
  }
}