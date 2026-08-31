package com.safo.icos.modules.talent.offboarding.domain.model.enums;

public enum OffboardingProcessStatus {
  /** Process created, waiting for approval */
  PENDING_APPROVAL,
  /** HR interview / exit interview stage */
  HR_INTERVIEW,
  /** Knowledge transfer in progress */
  KNOWLEDGE_TRANSFER,
  /** IT revoke accesses, collect equipment */
  IT_REVOKE,
  /** Financial settlement */
  FINANCIAL_SETTLEMENT,
  /** All tasks completed, ready for final sign-off */
  READY_FOR_SIGN_OFF,
  /** Process successfully completed */
  COMPLETED,
  /** Process was cancelled (employee stays) */
  CANCELLED
}