package com.safo.icos.modules.identity.invite.domain.model;

import com.safo.icos.modules.identity.invite.domain.model.enums.InviteStatus;
import com.safo.icos.modules.org.domain.model.enums.RoleName;

import java.time.OffsetDateTime;
import java.util.UUID;

import lombok.Builder;
import lombok.Getter;

/**
 * Domain entity representing an invitation to join a company.
 * Tracks the invitee's email, assigned role, target department,
 * and the current status. Supports {@link #accept()} and {@link #cancel()}
 * state transitions with validation.
 */
@Getter
@Builder
public class Invite {
  private final UUID id;
  private final String email;
  private final RoleName role;
  private final UUID companyId;
  private final UUID departmentId;
  private final UUID managerId;
  private final UUID invitedById;
  /** Hashed token value. Raw token is sent to the user via email. */
  private final String token;
  private final OffsetDateTime expiresAt;
  private final OffsetDateTime createdAt;
  private InviteStatus status;
  private OffsetDateTime updatedAt;

  /**
   * Checks whether this invite has passed its expiration date.
   *
   * @return {@code true} if the current time is after {@link #expiresAt}
   */
  public boolean isExpired() {
    return OffsetDateTime.now().isAfter(expiresAt);
  }

  /**
   * Checks whether this invite is in {@link InviteStatus#PENDING} state.
   *
   * @return {@code true} if pending
   */
  public boolean isPending() {
    return status == InviteStatus.PENDING;
  }

  /**
   * Checks whether this invite is no longer pending (accepted, expired, or cancelled).
   *
   * @return {@code true} if not pending
   */
  public boolean isNotPending() {
    return !isPending();
  }

  /**
   * Accepts this invitation.
   * Sets the status to {@link InviteStatus#ACCEPTED}.
   *
   * @throws IllegalStateException if the invite is not pending or has expired
   */
  public void accept() {
    if (isNotPending()) {
      throw new IllegalStateException("Only pending invites can be accepted. Current status: " + status);
    }
    if (isExpired()) {
      this.status = InviteStatus.EXPIRED;
      throw new IllegalStateException("Invite has expired");
    }
    this.status = InviteStatus.ACCEPTED;
    this.updatedAt = OffsetDateTime.now();
  }

  /**
   * Cancels this invitation.
   * Sets the status to {@link InviteStatus#CANCELED}.
   *
   * @throws IllegalStateException if the invite is not pending
   */
  public void cancel() {
    if (isNotPending()) {
      throw new IllegalStateException("Only pending invites can be canceled");
    }
    this.status = InviteStatus.CANCELED;
    this.updatedAt = OffsetDateTime.now();
  }
}