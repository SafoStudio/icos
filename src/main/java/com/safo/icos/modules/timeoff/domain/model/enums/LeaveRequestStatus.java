package com.safo.icos.modules.timeoff.domain.model.enums;

/** Possible statuses of a leave request. */
public enum LeaveRequestStatus {
  /** Request is being drafted, not yet submitted. */
  DRAFT,
  /** Request has been submitted and is awaiting approval. */
  PENDING,
  /** Request has been approved by the manager. */
  APPROVED,
  /** Request has been rejected by the manager. */
  REJECTED,
  /** Request has been cancelled by the employee. */
  CANCELLED
}