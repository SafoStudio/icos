package com.safo.icos.security.permission.rule;

import com.safo.icos.security.context.IcosUserPrincipal;
import com.safo.icos.security.permission.PolicyResult;
import com.safo.icos.security.permission.PolicyRule;
import org.springframework.stereotype.Component;

/**
 * Policy rule for onboarding pipeline actions ({@code onboarding:*}).
 * <p>
 * ADMIN and OWNER have full access.
 * HR department members and users with HR role have full access to manage onboarding.
 * Regular employees have access to complete their own assigned onboarding tasks.
 */
@Component
public class OnboardingPolicyRule implements PolicyRule {

  @Override
  public boolean supports(String action) {
    return action.startsWith("onboarding:");
  }

  @Override
  public PolicyResult check(IcosUserPrincipal user, String action, Object resource) {
    if ("ADMIN".equals(user.getRoleName()) || "OWNER".equals(user.getRoleName())) {
      return PolicyResult.allow("Admin/Owner has full access to onboarding pipeline");
    }

    if (isHr(user)) {
      return PolicyResult.allow("HR department members can manage onboarding pipeline");
    }

    if ("onboarding:task.complete".equals(action)) {
      return PolicyResult.allow("Employees can complete assigned onboarding tasks");
    }

    return PolicyResult.deny("Only ADMIN, OWNER, or HR department members can access onboarding pipeline");
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
