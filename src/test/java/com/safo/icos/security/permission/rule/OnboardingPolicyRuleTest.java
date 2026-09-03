package com.safo.icos.security.permission.rule;

import com.safo.icos.security.context.IcosUserPrincipal;
import com.safo.icos.security.permission.PolicyResult;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class OnboardingPolicyRuleTest {

  private final OnboardingPolicyRule rule = new OnboardingPolicyRule();

  @Test
  void shouldSupportOnboardingActions() {
    assertThat(rule.supports("onboarding:read")).isTrue();
    assertThat(rule.supports("onboarding:manage")).isTrue();
    assertThat(rule.supports("onboarding:task.complete")).isTrue();
  }

  @Test
  void shouldNotSupportOtherActions() {
    assertThat(rule.supports("user:edit")).isFalse();
    assertThat(rule.supports("recruitment:create")).isFalse();
  }

  @Test
  void shouldAllowAdmin() {
    IcosUserPrincipal admin = principalWithRoleAndDept("ADMIN", null);
    PolicyResult result = rule.check(admin, "onboarding:manage", null);
    assertThat(result.effect()).isEqualTo(PolicyResult.Effect.ALLOW);
  }

  @Test
  void shouldAllowOwner() {
    IcosUserPrincipal owner = principalWithRoleAndDept("OWNER", null);
    PolicyResult result = rule.check(owner, "onboarding:read", null);
    assertThat(result.effect()).isEqualTo(PolicyResult.Effect.ALLOW);
  }

  @Test
  void shouldAllowHrDepartmentMember() {
    IcosUserPrincipal hrMember = principalWithRoleAndDept("SPECIALIST", "HR");
    PolicyResult result = rule.check(hrMember, "onboarding:manage", null);
    assertThat(result.effect()).isEqualTo(PolicyResult.Effect.ALLOW);
  }

  @Test
  void shouldAllowHrRole() {
    IcosUserPrincipal hrRole = principalWithRoleAndDept("HR", null);
    PolicyResult result = rule.check(hrRole, "onboarding:manage", null);
    assertThat(result.effect()).isEqualTo(PolicyResult.Effect.ALLOW);
  }

  @Test
  void shouldAllowRegularEmployeeToCompleteTask() {
    IcosUserPrincipal employee = principalWithRoleAndDept("SPECIALIST", "Engineering");
    PolicyResult result = rule.check(employee, "onboarding:task.complete", null);
    assertThat(result.effect()).isEqualTo(PolicyResult.Effect.ALLOW);
  }

  @Test
  void shouldDenyNonHrManagingOnboarding() {
    IcosUserPrincipal engineer = principalWithRoleAndDept("SPECIALIST", "Engineering");
    PolicyResult result = rule.check(engineer, "onboarding:manage", null);
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
