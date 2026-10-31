package com.safo.icos.security.permission.rule;

import com.safo.icos.security.context.IcosUserPrincipal;
import com.safo.icos.security.permission.PolicyResult;
import com.safo.icos.security.permission.PolicyRule;
import org.springframework.stereotype.Component;

/**
 * Policy rule that grants full access to users with the ADMIN role,
 * except for actions strictly restricted to OWNER (such as email:change).
 * Runs first (highest priority).
 */
@Component
public class AdminPolicyRule implements PolicyRule {

  @Override
  public boolean supports(String action) {
    return !"email:change".equals(action);
  }

  @Override
  public PolicyResult check(IcosUserPrincipal user, String action, Object resource) {
    if ("ADMIN".equals(user.getRoleName())) {
      return PolicyResult.allow("Admin has full access");
    }
    return PolicyResult.abstain();
  }

  @Override
  public int getOrder() {
    return 0; // Highest priority
  }
}
