package com.safo.icos.security.permission.rule;

import com.safo.icos.security.context.IcosUserPrincipal;
import com.safo.icos.security.permission.PolicyResult;
import com.safo.icos.security.permission.PolicyRule;
import org.springframework.stereotype.Component;

@Component
public class DepartmentPolicyRule implements PolicyRule {
    @Override
    public boolean supports(String action) {
        return action.startsWith("users:") || action.startsWith("departments:");
    }

    @Override
    public PolicyResult check(IcosUserPrincipal user, String action, Object resource) {
        if ("OWNER".equals(user.getRoleName())) {
            return PolicyResult.allow("Owner has full access within company");
        }

        if ("HEAD".equals(user.getRoleName())) {
            if (isHrDepartment(user.getDepartmentName())) {
                return PolicyResult.allow("HR Head has cross-department access");
            }

            // Regular Head can only see their own department (placeholder for further logic)
            return PolicyResult.abstain(); 
        }

        return PolicyResult.abstain();
    }

    private boolean isHrDepartment(String deptName) {
        return deptName != null && (deptName.equalsIgnoreCase("HR") || deptName.toUpperCase().contains("HR"));
    }

    @Override
    public int getOrder() {
        return 50;
    }
}
