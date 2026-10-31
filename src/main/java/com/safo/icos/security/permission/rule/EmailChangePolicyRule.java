package com.safo.icos.security.permission.rule;

import com.safo.icos.security.context.IcosUserPrincipal;
import com.safo.icos.security.permission.PolicyResult;
import com.safo.icos.security.permission.PolicyRule;
import org.springframework.stereotype.Component;

/**
 * Policy rule for email change actions.
 * Only the OWNER role is permitted to change email addresses.
 * Regular users, HR, and Admins cannot change emails because the email
 * serves as the primary system login and identity key.
 */
@Component
public class EmailChangePolicyRule implements PolicyRule {

  @Override
  public boolean supports(String action) {
    return "email:change".equals(action);
  }

  @Override
  public PolicyResult check(IcosUserPrincipal user, String action, Object resource) {
    if ("OWNER".equals(user.getRoleName())) {
      return PolicyResult.allow("Workspace Owner has permission to change user emails");
    }
    return PolicyResult.deny("Only the workspace OWNER can change email addresses");
  }

  @Override
  public int getOrder() {
    return 10;
  }
}
