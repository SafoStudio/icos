package com.safo.icos.modules.identity.user.domain.exception;

import com.safo.icos.common.exception.BusinessException;
import com.safo.icos.modules.identity.user.api.constant.UserErrorCode;
import com.safo.icos.modules.identity.user.domain.model.Email;

import java.util.UUID;

/**
 * Thrown when attempting to create a user with an email that is already registered.
 */
public class EmailAlreadyExistsException extends BusinessException {

  public EmailAlreadyExistsException(Email email) {
    super("Email already exists: " + email, UserErrorCode.EMAIL_ALREADY_EXISTS);
  }

  public EmailAlreadyExistsException(Email email, UUID companyId) {
    super("Email already exists: " + email + " for company: " + companyId,
            UserErrorCode.EMAIL_ALREADY_EXISTS);
  }
}