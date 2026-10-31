package com.safo.icos.security.permission.rule;

import com.safo.icos.security.context.IcosUserPrincipal;
import com.safo.icos.security.permission.PolicyResult;
import com.safo.icos.security.permission.PolicyRule;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class UserManagementPolicyRule implements PolicyRule {

  @Override
  public boolean supports(String action) {
    return "user:edit".equals(action);
  }

  @Override
  public PolicyResult check(IcosUserPrincipal user, String action, Object resource) {
    if ("OWNER".equals(user.getRoleName()) || "ADMIN".equals(user.getRoleName())) {
      return PolicyResult.allow("Owner/Admin has full access to user management");
    }

    if (SecurityRuleUtils.isHrUser(user.getRoleName(), user.getDepartmentName())) {
      return PolicyResult.allow("HR department can manage users");
    }

    log.warn("User {} with role {} and department {} tried to manage users",
            user.getEmail(), user.getRoleName(), user.getDepartmentName());
    return PolicyResult.deny("Only HR, Owner, or Admin can manage user roles and departments");
  }

  @Override
  public int getOrder() {
    return 25;
  }
}
