package com.safo.icos.security.permission.rule;

import com.safo.icos.security.context.IcosUserPrincipal;
import com.safo.icos.security.permission.PolicyResult;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class RecruitmentPolicyRuleTest {

  private final RecruitmentPolicyRule rule = new RecruitmentPolicyRule();

  @Test
  void shouldSupportRecruitmentActions() {
    assertThat(rule.supports("recruitment:create")).isTrue();
    assertThat(rule.supports("application:review")).isTrue();
    assertThat(rule.supports("interview:schedule")).isTrue();
    assertThat(rule.supports("vacancy:read")).isTrue();
    assertThat(rule.supports("candidate:manage")).isTrue();
    assertThat(rule.supports("pipeline:read")).isTrue();
    assertThat(rule.supports("pipeline:manage")).isTrue();
  }

  @Test
  void shouldNotSupportOtherActions() {
    assertThat(rule.supports("user:edit")).isFalse();
    assertThat(rule.supports("department:create")).isFalse();
  }

  @Test
  void shouldAllowAdmin() {
    IcosUserPrincipal admin = principalWithRoleAndDept("ADMIN", null);

    PolicyResult result = rule.check(admin, "recruitment:create", null);

    assertThat(result.effect()).isEqualTo(PolicyResult.Effect.ALLOW);
  }

  @Test
  void shouldAllowOwner() {
    IcosUserPrincipal owner = principalWithRoleAndDept("OWNER", null);

    PolicyResult result = rule.check(owner, "recruitment:create", null);

    assertThat(result.effect()).isEqualTo(PolicyResult.Effect.ALLOW);
  }

  @Test
  void shouldAllowHrMember() {
    IcosUserPrincipal hrMember = principalWithRoleAndDept("SPECIALIST", "HR");

    PolicyResult result = rule.check(hrMember, "application:review", null);

    assertThat(result.effect()).isEqualTo(PolicyResult.Effect.ALLOW);
  }

  @Test
  void shouldAllowHrRole() {
    IcosUserPrincipal hrRole = principalWithRoleAndDept("HR", null);

    PolicyResult result = rule.check(hrRole, "pipeline:manage", null);

    assertThat(result.effect()).isEqualTo(PolicyResult.Effect.ALLOW);
  }

  @Test
  void shouldAllowPipelineActionForOwnerAndAdmin() {
    IcosUserPrincipal admin = principalWithRoleAndDept("ADMIN", null);
    IcosUserPrincipal owner = principalWithRoleAndDept("OWNER", null);

    assertThat(rule.check(admin, "pipeline:read", null).effect()).isEqualTo(PolicyResult.Effect.ALLOW);
    assertThat(rule.check(owner, "pipeline:manage", null).effect()).isEqualTo(PolicyResult.Effect.ALLOW);
  }

  @Test
  void shouldDenyNonHrSpecialist() {
    IcosUserPrincipal engineer = principalWithRoleAndDept("SPECIALIST", "Engineering");

    PolicyResult result = rule.check(engineer, "recruitment:create", null);

    assertThat(result.effect()).isEqualTo(PolicyResult.Effect.DENY);
  }

  @Test
  void shouldDenyHeadOfNonHrDepartment() {
    IcosUserPrincipal engineeringHead = principalWithRoleAndDept("HEAD", "Engineering");

    PolicyResult result = rule.check(engineeringHead, "interview:schedule", null);

    assertThat(result.effect()).isEqualTo(PolicyResult.Effect.DENY);
  }

  private IcosUserPrincipal principalWithRoleAndDept(String role, String departmentName) {
    return IcosUserPrincipal.builder()
            .userId(UUID.randomUUID()).email("test@test.com")
            .roleName(role).departmentName(departmentName)
            .authorities(List.of()).build();
  }
}
