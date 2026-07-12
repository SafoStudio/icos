package com.safo.icos.security.permission.rule;

import com.safo.icos.security.context.IcosUserPrincipal;
import com.safo.icos.security.permission.PolicyResult;
import com.safo.icos.security.permission.PolicyRule;
import java.util.Objects;
import java.util.UUID;
import org.springframework.stereotype.Component;

/**
 * Policy rule for profile editing ({@code profile:edit}).
 * <p>
 * Access is granted if:
 * <ul>
 *   <li>The user edits their own profile and has the {@code profile:edit.self} permission.</li>
 *   <li>The user has the {@code profile:edit.all} permission (Admin/Owner/HR).</li>
 *   <li>The user is an HR department HEAD.</li>
 * </ul>
 */
@Component
public class ProfilePolicyRule implements PolicyRule {

  @Override
  public boolean supports(String action) {
    return "profile:edit".equals(action);
  }

  @Override
  public PolicyResult check(IcosUserPrincipal user, String action, Object resource) {
    if (!(resource instanceof UUID targetUserId)) {
      return PolicyResult.abstain();
    }

    // User editing their own profile
    if (user.getUserId().equals(targetUserId)
            && user.getAuthorities().stream()
            .anyMatch(a -> Objects.equals(a.getAuthority(), "profile:edit.self"))) {
      return PolicyResult.allow("User can edit their own profile");
    }

    // User has full profile editing permission
    if (user.getAuthorities().stream()
            .anyMatch(a -> Objects.equals(a.getAuthority(), "profile:edit.all"))) {
      return PolicyResult.allow("User has permission to edit all profiles");
    }

    // HR department head
    if ("HEAD".equals(user.getRoleName()) && isHrDepartment(user.getDepartmentName())) {
      return PolicyResult.allow("HR Head can edit employee profiles");
    }

    return PolicyResult.deny("No permission to edit this profile");
  }

  private boolean isHrDepartment(String deptName) {
    return deptName != null
            && (deptName.equalsIgnoreCase("HR") || deptName.toUpperCase().contains("HR"));
  }

  @Override
  public int getOrder() {
    return 20;
  }
}