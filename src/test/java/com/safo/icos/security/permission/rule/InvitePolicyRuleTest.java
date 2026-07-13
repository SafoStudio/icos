package com.safo.icos.security.permission.rule;

import com.safo.icos.security.context.IcosUserPrincipal;
import com.safo.icos.security.permission.PolicyResult;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class InvitePolicyRuleTest {

  private final InvitePolicyRule rule = new InvitePolicyRule();

  @Test
  void shouldSupportInviteManageAction() {
    assertThat(rule.supports("invite:manage")).isTrue();
    assertThat(rule.supports("user:edit")).isFalse();
  }

  @Test
  void shouldAllowAdmin() {
    IcosUserPrincipal admin = principalWithRole("ADMIN", null);

    PolicyResult result = rule.check(admin, "invite:manage", null);

    assertThat(result.effect()).isEqualTo(PolicyResult.Effect.ALLOW);
  }

  @Test
  void shouldAllowOwner() {
    IcosUserPrincipal owner = principalWithRole("OWNER", null);

    PolicyResult result = rule.check(owner, "invite:manage", null);

    assertThat(result.effect()).isEqualTo(PolicyResult.Effect.ALLOW);
  }

  @Test
  void shouldAllowHrDepartmentMember() {
    IcosUserPrincipal hrSpecialist = principalWithRole("SPECIALIST", "HR");

    PolicyResult result = rule.check(hrSpecialist, "invite:manage", null);

    assertThat(result.effect()).isEqualTo(PolicyResult.Effect.ALLOW);
  }

  @Test
  void shouldDenyNonHrSpecialist() {
    IcosUserPrincipal engineer = principalWithRole("SPECIALIST", "Engineering");

    PolicyResult result = rule.check(engineer, "invite:manage", null);

    assertThat(result.effect()).isEqualTo(PolicyResult.Effect.DENY);
  }

  @Test
  void shouldDenyHeadOfNonHrDepartment() {
    IcosUserPrincipal engineeringHead = principalWithRole("HEAD", "Engineering");

    PolicyResult result = rule.check(engineeringHead, "invite:manage", null);

    assertThat(result.effect()).isEqualTo(PolicyResult.Effect.DENY);
  }

  private IcosUserPrincipal principalWithRole(String role, String departmentName) {
    return IcosUserPrincipal.builder()
            .userId(UUID.randomUUID())
            .email("test@test.com")
            .roleName(role)
            .departmentName(departmentName)
            .authorities(List.of())
            .build();
  }
}