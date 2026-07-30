package com.safo.icos.modules.talent.onboarding.domain.model.enums;

/** Possible statuses of an onboarding process. */
public enum OnboardingProcessStatus {
  /** Process created but not yet started. */
  NOT_STARTED,
  /** Onboarding is actively in progress. */
  IN_PROGRESS,
  /** All tasks completed successfully. */
  COMPLETED,
  /** Progress has stalled or is blocked. */
  STALLED
}