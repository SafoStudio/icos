package com.safo.icos.modules.org.domain.model.enums;

/**
 * System-wide role names.
 * Used for both invitations and user memberships.
 */
public enum RoleName {
  /** System technical administrator. */
  ADMIN,
  /** Company owner — has full control over their company. */
  OWNER,
  /** Department head — manages a specific department. */
  HEAD,
  /** Regular department member. */
  SPECIALIST
}
