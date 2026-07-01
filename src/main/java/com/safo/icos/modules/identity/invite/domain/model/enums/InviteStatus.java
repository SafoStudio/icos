package com.safo.icos.modules.identity.invite.domain.model.enums;

/**
 * Possible states of an invitation.
 */
public enum InviteStatus {
  /** Invitation is awaiting acceptance. */
  PENDING,
  /** Invitation has been accepted by the user. */
  ACCEPTED,
  /** Invitation has expired before being accepted. */
  EXPIRED,
  /** Invitation has been manually cancelled by an administrator. */
  CANCELED
}