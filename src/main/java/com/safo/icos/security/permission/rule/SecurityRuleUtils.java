package com.safo.icos.security.permission.rule;

/**
 * Utility methods for evaluating roles, permissions, and department-based access.
 */
public final class SecurityRuleUtils {

  private SecurityRuleUtils() {
  }

  /**
   * Checks if the department name corresponds to Human Resources.
   *
   * @param departmentName name of the department
   * @return true if department is HR
   */
  public static boolean isHrDepartment(String departmentName) {
    if (departmentName == null) {
      return false;
    }
    String normalized = departmentName.trim().toUpperCase();
    return normalized.contains("HR")
        || normalized.contains("HUMAN RESOURCE")
        || normalized.contains("RECRUIT")
        || normalized.contains("КАДР")
        || normalized.contains("ПЕРСОНАЛ");
  }

  /**
   * Checks if a user has HR capabilities based on role or department.
   *
   * @param roleName role of the user (e.g. OWNER, ADMIN, HEAD, SPECIALIST)
   * @param departmentName name of the user's department
   * @return true if user has HR privileges
   */
  public static boolean isHrUser(String roleName, String departmentName) {
    if ("ADMIN".equalsIgnoreCase(roleName)
        || "OWNER".equalsIgnoreCase(roleName)
        || "HR".equalsIgnoreCase(roleName)) {
      return true;
    }
    return isHrDepartment(departmentName);
  }
}
