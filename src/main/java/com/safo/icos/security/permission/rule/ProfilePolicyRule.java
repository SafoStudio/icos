package com.safo.icos.security.permission.rule;

import com.safo.icos.security.context.IcosUserPrincipal;
import com.safo.icos.security.permission.PolicyResult;
import com.safo.icos.security.permission.PolicyRule;
import org.springframework.stereotype.Component;
import java.util.UUID;

@Component
public class ProfilePolicyRule implements PolicyRule {
    @Override
    public boolean supports(String action) {
        return "profile:edit".equals(action);
    }

    @Override
    public PolicyResult check(IcosUserPrincipal user, String action, Object resource) {
        // resource is expected to be the target userId (UUID)
        if (!(resource instanceof UUID targetUserId)) {
            return PolicyResult.abstain();
        }

        // 1. Check if editing own profile
        if (user.getUserId().equals(targetUserId)) {
            if (user.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("profile:edit.self"))) {
                return PolicyResult.allow("User can edit their own profile");
            }
        }

        // 2. Check if user has permission to edit all profiles (Admin/Owner/HR)
        if (user.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("profile:edit.all"))) {
            return PolicyResult.allow("User has permission to edit all profiles");
        }

        // 3. HR logic (if HR Head, they can edit profiles in the company)
        if ("HEAD".equals(user.getRoleName()) && isHrDepartment(user.getDepartmentName())) {
            return PolicyResult.allow("HR Head can edit employee profiles");
        }

        return PolicyResult.deny("No permission to edit this profile");
    }

    private boolean isHrDepartment(String deptName) {
        return deptName != null && (deptName.equalsIgnoreCase("HR") || deptName.toUpperCase().contains("HR"));
    }

    @Override
    public int getOrder() {
        return 20;
    }
}
