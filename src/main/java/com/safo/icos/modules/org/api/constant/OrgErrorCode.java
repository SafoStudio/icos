package com.safo.icos.modules.org.api.constant;

/**
 * Organisation management error codes.
 */
public final class OrgErrorCode {
  /** Department with this name already exists in the company. */
  public static final String DEPT_ALREADY_EXISTS = "ORG_001";
  /** Department not found by ID. */
  public static final String DEPT_NOT_FOUND = "ORG_002";
  /** Position with this name already exists in the company. */
  public static final String POSITION_ALREADY_EXISTS = "ORG_003";
  /** Position not found by ID. */
  public static final String POSITION_NOT_FOUND = "ORG_004";

  private OrgErrorCode() {
  }
}