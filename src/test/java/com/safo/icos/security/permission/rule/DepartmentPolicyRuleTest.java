package com.safo.icos.security.permission.rule;

import com.safo.icos.security.context.IcosUserPrincipal;
import com.safo.icos.security.permission.PolicyResult;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class DepartmentPolicyRuleTest {

  private final DepartmentPolicyRule rule = new DepartmentPolicyRule();

  @Test
  void shouldSupportDepartmentActions() {
    assertThat(rule.supports("department:create")).isTrue();
    assertThat(rule.supports("department:manage")).isTrue();
    assertThat(rule.supports("department:delete")).isTrue();
  }

  @Test
  void shouldNotSupportOtherActions() {
    assertThat(rule.supports("user:edit")).isFalse();
    assertThat(rule.supports("position:create")).isFalse();
  }

  @Test
  void shouldAllowOwner() {
    IcosUserPrincipal owner = principalWithRole("OWNER");

    PolicyResult result = rule.check(owner, "department:create", null);

    assertThat(result.effect()).isEqualTo(PolicyResult.Effect.ALLOW);
  }

  @Test
  void shouldAbstainForAdmin() {
    IcosUserPrincipal admin = principalWithRole("ADMIN");

    PolicyResult result = rule.check(admin, "department:create", null);

    assertThat(result.effect()).isEqualTo(PolicyResult.Effect.ABSTAIN);
  }

  @Test
  void shouldAbstainForHead() {
    IcosUserPrincipal head = principalWithRole("HEAD");

    PolicyResult result = rule.check(head, "department:create", null);

    assertThat(result.effect()).isEqualTo(PolicyResult.Effect.ABSTAIN);
  }

  private IcosUserPrincipal principalWithRole(String role) {
    return IcosUserPrincipal.builder()
            .userId(UUID.randomUUID()).email("test@test.com")
            .roleName(role).authorities(List.of()).build();
  }
}
