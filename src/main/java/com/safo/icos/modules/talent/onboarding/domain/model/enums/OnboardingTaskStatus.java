package com.safo.icos.modules.talent.onboarding.domain.model.enums;

/** Possible statuses of an onboarding task. */
public enum OnboardingTaskStatus {
  /** Task is awaiting completion. */
  PENDING,
  /** Task has been completed. */
  DONE,
  /** Task is waiting on something before it can proceed. */
  WAITING
}