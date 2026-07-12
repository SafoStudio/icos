package com.safo.icos.security.permission.rule;

import com.safo.icos.security.context.IcosUserPrincipal;
import com.safo.icos.security.permission.PolicyResult;
import com.safo.icos.security.permission.PolicyRule;
import org.springframework.stereotype.Component;

/**
 * Policy rule for recruitment-related actions
 * ({@code recruitment:*}, {@code application:*}, {@code interview:*}.
 * <p>
 * ADMIN and OWNER always have access.
 * HR department members also have access.
 * Hiring manager logic based on vacancy assignments is planned but not yet implemented.
 */
@Component
public class RecruitmentPolicyRule implements PolicyRule {

  @Override
  public boolean supports(String action) {
    return action.startsWith("recruitment:")
            || action.startsWith("application:")
            || action.startsWith("interview:");
  }

  @Override
  public PolicyResult check(IcosUserPrincipal user, String action, Object resource) {
    if ("ADMIN".equals(user.getRoleName()) || "OWNER".equals(user.getRoleName())) {
      return PolicyResult.allow("Admin/Owner has full access to recruitment");
    }

    if (user.getDepartmentName() != null
            && user.getDepartmentName().equalsIgnoreCase("HR")) {
      return PolicyResult.allow("HR department members can manage recruitment");
    }

    // TODO: Add Hiring Manager specific logic based on VacancyAssignments

    return PolicyResult.deny(
            "Only ADMIN, OWNER or HR department members can manage recruitment");
  }

  @Override
  public int getOrder() {
    return 10;
  }
}