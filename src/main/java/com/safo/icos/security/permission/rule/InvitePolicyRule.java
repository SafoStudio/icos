package com.safo.icos.security.permission.rule;

import com.safo.icos.security.context.IcosUserPrincipal;
import com.safo.icos.security.permission.PolicyResult;
import com.safo.icos.security.permission.PolicyRule;
import org.springframework.stereotype.Component;

@Component
public class InvitePolicyRule implements PolicyRule {
  @Override
  public boolean supports(String action) {
    return "invite:manage".equals(action);
  }

  @Override
  public PolicyResult check(IcosUserPrincipal user, String action, Object resource) {
    // ADMIN and OWNER always have access
    if ("ADMIN".equals(user.getRoleName()) || "OWNER".equals(user.getRoleName())) {
      return PolicyResult.allow("Admin/Owner has full access to invitations");
    }

    // HR department members have access regardless of role (HEAD or SPECIALIST)
    if (user.getDepartmentName() != null && user.getDepartmentName().equalsIgnoreCase("HR")) {
      return PolicyResult.allow("HR department members can manage invitations");
    }

    return PolicyResult.deny("Only ADMIN, OWNER or HR department members can manage invitations");
  }

  @Override
  public int getOrder() {
    return 10;
  }
}
