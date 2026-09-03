package com.safo.icos.security.permission.rule;

import com.safo.icos.security.context.IcosUserPrincipal;
import com.safo.icos.security.permission.PolicyResult;
import com.safo.icos.security.permission.PolicyRule;
import org.springframework.stereotype.Component;

/**
 * Policy rule for offboarding pipeline actions ({@code offboarding:*}).
 * <p>
 * ADMIN and OWNER have full access.
 * HR department members and users with HR role have full access to manage offboarding.
 * Assignees have access to view and complete their assigned offboarding tasks.
 */
@Component
public class OffboardingPolicyRule implements PolicyRule {

  @Override
  public boolean supports(String action) {
    return action.startsWith("offboarding:");
  }

  @Override
  public PolicyResult check(IcosUserPrincipal user, String action, Object resource) {
    if ("ADMIN".equals(user.getRoleName()) || "OWNER".equals(user.getRoleName())) {
      return PolicyResult.allow("Admin/Owner has full access to offboarding pipeline");
    }

    if (isHr(user)) {
      return PolicyResult.allow("HR department members can manage offboarding pipeline");
    }

    if (action.startsWith("offboarding:task.")) {
      return PolicyResult.allow("Assignees can view and complete assigned offboarding tasks");
    }

    return PolicyResult.deny("Only ADMIN, OWNER, or HR department members can access offboarding pipeline");
  }

  private boolean isHr(IcosUserPrincipal user) {
    if ("HR".equalsIgnoreCase(user.getRoleName())) {
      return true;
    }
    return user.getDepartmentName() != null
            && user.getDepartmentName().toUpperCase().contains("HR");
  }

  @Override
  public int getOrder() {
    return 10;
  }
}
