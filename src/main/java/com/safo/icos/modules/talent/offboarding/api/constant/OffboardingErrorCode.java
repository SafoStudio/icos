package com.safo.icos.modules.talent.offboarding.api.constant;

public final class OffboardingErrorCode {
  public static final String RESOURCE_NOT_FOUND = "OFF_001";
  public static final String EMPLOYEE_ALREADY_OFFBOARDING = "OFF_002";
  public static final String TASK_ALREADY_COMPLETED = "OFF_003";
  public static final String CANT_REVERT_COMPLETED = "OFF_004";
  public static final String TASK_NOT_PENDING = "OFF_005";
  public static final String TASK_ALREADY_SKIPPED = "OFF_006";
  public static final String CANT_CANCEL_COMPLETED = "OFF_007";
  public static final String PROCESS_ALREADY_CANCELLED = "OFF_008";
  public static final String NO_EMPLOYEE_FOR_ROLE = "OFF_009";
  public static final String UNKNOWN_ROLE = "OFF_010";
  public static final String TASK_BLOCKED = "OFF_011";
  public static final String TASK_NOT_BLOCKED = "OFF_012";
  public static final String INVALID_STATUS_TRANSITION = "OFF_013";
  public static final String CANT_TRANSITION_COMPLETED = "OFF_014";
  public static final String CANT_TRANSITION_CANCELLED = "OFF_015";

  private OffboardingErrorCode() {}
}