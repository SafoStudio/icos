package com.safo.icos.security.permission.rule;

import com.safo.icos.security.context.IcosUserPrincipal;
import com.safo.icos.security.permission.PolicyResult;
import com.safo.icos.security.permission.PolicyRule;
import org.springframework.stereotype.Component;

/**
 * Policy rule for email change actions.
 * Only OWNER and ADMIN roles are permitted; all others are explicitly denied.
 */
@Component
public class EmailChangePolicyRule implements PolicyRule {

  @Override
  public boolean supports(String action) {
    return "email:change".equals(action);
  }

  @Override
  public PolicyResult check(IcosUserPrincipal user, String action, Object resource) {
    if ("OWNER".equals(user.getRoleName()) || "ADMIN".equals(user.getRoleName())) {
      return PolicyResult.allow("Owners and Admins can change emails");
    }
    return PolicyResult.deny("Only Owners and Admins can change emails");
  }

  @Override
  public int getOrder() {
    return 10;
  }
}