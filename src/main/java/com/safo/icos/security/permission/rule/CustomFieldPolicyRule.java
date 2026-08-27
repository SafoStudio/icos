package com.safo.icos.security.permission.rule;

import com.safo.icos.security.context.IcosUserPrincipal;
import com.safo.icos.security.permission.PolicyResult;
import com.safo.icos.security.permission.PolicyRule;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Policy rule for custom field schema definitions and field values ({@code customfield:*}).
 * <p>
 * - {@code customfield:read}: Allowed for any authenticated user in the workspace to power dynamic forms.
 * - {@code customfield:manage}: Allowed for ADMIN, OWNER, HR department members, or users with explicit manage authority.
 */
@Slf4j
@Component
public class CustomFieldPolicyRule implements PolicyRule {

  @Override
  public boolean supports(String action) {
    return action != null && action.startsWith("customfield:");
  }

  @Override
  public PolicyResult check(IcosUserPrincipal user, String action, Object resource) {
    if (user == null) {
      return PolicyResult.deny("User must be authenticated");
    }

    // Direct authority match from database / JWT
    if (user.getAuthorities().stream()
            .anyMatch(a -> Objects.equals(a.getAuthority(), action))) {
      return PolicyResult.allow("User has direct authority for " + action);
    }

    // Read access to schema definitions & field values is permitted for all authenticated members
    if ("customfield:read".equals(action)) {
      return PolicyResult.allow("All authenticated members can read custom fields");
    }

    // Schema management: ADMIN, OWNER, HR department, or users with manage permissions
    if ("customfield:manage".equals(action)) {
      if ("ADMIN".equals(user.getRoleName()) || "OWNER".equals(user.getRoleName())) {
        return PolicyResult.allow("Admin/Owner has full access to custom field definitions");
      }

      if (isHrDepartment(user.getDepartmentName())) {
        return PolicyResult.allow("HR department members can manage custom field definitions");
      }

      return PolicyResult.deny("Only Admin, Owner, or HR department members can manage custom field definitions");
    }

    return PolicyResult.abstain();
  }

  private boolean isHrDepartment(String departmentName) {
    return departmentName != null && departmentName.toUpperCase().contains("HR");
  }

  @Override
  public int getOrder() {
    return 30;
  }
}