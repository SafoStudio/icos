package com.safo.icos.security.permission.rule;

import com.safo.icos.security.context.IcosUserPrincipal;
import com.safo.icos.security.permission.PolicyResult;
import com.safo.icos.security.permission.PolicyRule;
import org.springframework.stereotype.Component;

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

    return PolicyResult.abstain();
  }

  @Override
  public int getOrder() {
    return 50;
  }
}