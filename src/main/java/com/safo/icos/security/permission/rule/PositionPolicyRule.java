package com.safo.icos.security.permission.rule;

import com.safo.icos.security.context.IcosUserPrincipal;
import com.safo.icos.security.permission.PolicyResult;
import com.safo.icos.security.permission.PolicyRule;
import org.springframework.stereotype.Component;

/**
 * Policy rule for position-related actions ({@code position:*}).
 * Grants full access to OWNER role and users with granted authorities.
 */
@Component
public class PositionPolicyRule implements PolicyRule {

  @Override
  public boolean supports(String action) {
    return action.startsWith("position:");
  }

  @Override
  public PolicyResult check(IcosUserPrincipal user, String action, Object resource) {
    if ("OWNER".equals(user.getRoleName())) {
      return PolicyResult.allow("Owner has full access to positions");
    }
    if (user.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals(action))) {
      return PolicyResult.allow("User has explicit permission: " + action);
    }
    return PolicyResult.abstain();
  }

  @Override
  public int getOrder() {
    return 50;
  }
}
