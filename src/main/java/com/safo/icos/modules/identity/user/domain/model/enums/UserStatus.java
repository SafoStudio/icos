package com.safo.icos.modules.identity.user.domain.model.enums;

/**
 * Possible states of a user account.
 */
public enum UserStatus {
  /** User is active and can access the system. */
  ACTIVE,
  /** User has been temporarily suspended. */
  SUSPENDED,
  /** User has been permanently terminated. */
  TERMINATED,
  /** User has been invited but not yet registered. */
  INVITED
}