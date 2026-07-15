package com.safo.icos.security.permission.rule;

import com.safo.icos.security.context.IcosUserPrincipal;
import com.safo.icos.security.permission.PolicyResult;
import com.safo.icos.security.permission.PolicyRule;
import org.springframework.stereotype.Component;

/**
 * Policy rule for invitation management ({@code invite:manage}).
 * ADMIN and OWNER always have access.
 * Members of the HR department also have access regardless of their role.
 */
@Component
public class InvitePolicyRule implements PolicyRule {

  @Override
  public boolean supports(String action) {
    return "invite:manage".equals(action);
  }

  @Override
  public PolicyResult check(IcosUserPrincipal user, String action, Object resource) {
    if ("ADMIN".equals(user.getRoleName()) || "OWNER".equals(user.getRoleName())) {
      return PolicyResult.allow("Admin/Owner has full access to invitations");
    }

    if (user.getDepartmentName() != null
            && user.getDepartmentName().equalsIgnoreCase("HR")) {
      return PolicyResult.allow("HR department members can manage invitations");
    }

    return PolicyResult.deny(
            "Only ADMIN, OWNER or HR department members can manage invitations");
  }

  @Override
  public int getOrder() {
    return 10;
  }
}
