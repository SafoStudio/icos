package com.safo.icos.security.permission.rule;

import com.safo.icos.security.context.IcosUserPrincipal;
import com.safo.icos.security.permission.PolicyResult;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class OffboardingPolicyRuleTest {

  private final OffboardingPolicyRule rule = new OffboardingPolicyRule();

  @Test
  void shouldSupportOffboardingActions() {
    assertThat(rule.supports("offboarding:read")).isTrue();
    assertThat(rule.supports("offboarding:manage")).isTrue();
    assertThat(rule.supports("offboarding:task.read")).isTrue();
    assertThat(rule.supports("offboarding:task.complete")).isTrue();
  }

  @Test
  void shouldNotSupportOtherActions() {
    assertThat(rule.supports("user:edit")).isFalse();
    assertThat(rule.supports("recruitment:create")).isFalse();
  }

  @Test
  void shouldAllowAdmin() {
    IcosUserPrincipal admin = principalWithRoleAndDept("ADMIN", null);
    PolicyResult result = rule.check(admin, "offboarding:manage", null);
    assertThat(result.effect()).isEqualTo(PolicyResult.Effect.ALLOW);
  }

  @Test
  void shouldAllowOwner() {
    IcosUserPrincipal owner = principalWithRoleAndDept("OWNER", null);
    PolicyResult result = rule.check(owner, "offboarding:read", null);
    assertThat(result.effect()).isEqualTo(PolicyResult.Effect.ALLOW);
  }

  @Test
  void shouldAllowHrDepartmentMember() {
    IcosUserPrincipal hrMember = principalWithRoleAndDept("SPECIALIST", "HR");
    PolicyResult result = rule.check(hrMember, "offboarding:manage", null);
    assertThat(result.effect()).isEqualTo(PolicyResult.Effect.ALLOW);
  }

  @Test
  void shouldAllowHrRole() {
    IcosUserPrincipal hrRole = principalWithRoleAndDept("HR", null);
    PolicyResult result = rule.check(hrRole, "offboarding:manage", null);
    assertThat(result.effect()).isEqualTo(PolicyResult.Effect.ALLOW);
  }

  @Test
  void shouldAllowAssigneeToViewAndCompleteTask() {
    IcosUserPrincipal itAdmin = principalWithRoleAndDept("SPECIALIST", "IT");
    PolicyResult result = rule.check(itAdmin, "offboarding:task.complete", null);
    assertThat(result.effect()).isEqualTo(PolicyResult.Effect.ALLOW);
  }

  @Test
  void shouldDenyNonHrManagingOffboarding() {
    IcosUserPrincipal engineer = principalWithRoleAndDept("SPECIALIST", "Engineering");
    PolicyResult result = rule.check(engineer, "offboarding:manage", null);
    assertThat(result.effect()).isEqualTo(PolicyResult.Effect.DENY);
  }

  private IcosUserPrincipal principalWithRoleAndDept(String role, String departmentName) {
    return IcosUserPrincipal.builder()
            .userId(UUID.randomUUID())
            .email("test@test.com")
            .roleName(role)
            .departmentName(departmentName)
            .authorities(List.of())
            .build();
  }
}
