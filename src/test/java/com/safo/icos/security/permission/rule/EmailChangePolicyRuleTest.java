package com.safo.icos.security.permission.rule;

import com.safo.icos.security.context.IcosUserPrincipal;
import com.safo.icos.security.permission.PolicyResult;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class EmailChangePolicyRuleTest {

  private final EmailChangePolicyRule rule = new EmailChangePolicyRule();

  @Test
  void shouldOnlySupportEmailChangeAction() {
    assertThat(rule.supports("email:change")).isTrue();
    assertThat(rule.supports("email:read")).isFalse();
    assertThat(rule.supports("user:edit")).isFalse();
  }

  @Test
  void shouldAllowAdmin() {
    IcosUserPrincipal admin = principalWithRole("ADMIN");

    PolicyResult result = rule.check(admin, "email:change", null);

    assertThat(result.effect()).isEqualTo(PolicyResult.Effect.ALLOW);
  }

  @Test
  void shouldAllowOwner() {
    IcosUserPrincipal owner = principalWithRole("OWNER");

    PolicyResult result = rule.check(owner, "email:change", null);

    assertThat(result.effect()).isEqualTo(PolicyResult.Effect.ALLOW);
  }

  @Test
  void shouldDenySpecialist() {
    IcosUserPrincipal specialist = principalWithRole("SPECIALIST");

    PolicyResult result = rule.check(specialist, "email:change", null);

    assertThat(result.effect()).isEqualTo(PolicyResult.Effect.DENY);
  }

  @Test
  void shouldDenyHead() {
    IcosUserPrincipal head = principalWithRole("HEAD");

    PolicyResult result = rule.check(head, "email:change", null);

    assertThat(result.effect()).isEqualTo(PolicyResult.Effect.DENY);
  }

  private IcosUserPrincipal principalWithRole(String role) {
    return IcosUserPrincipal.builder()
            .userId(UUID.randomUUID()).email("test@test.com")
            .roleName(role).authorities(List.of()).build();
  }
}
