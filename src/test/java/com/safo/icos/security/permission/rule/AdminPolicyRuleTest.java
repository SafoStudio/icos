package com.safo.icos.security.permission.rule;

import com.safo.icos.security.context.IcosUserPrincipal;
import com.safo.icos.security.permission.PolicyResult;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class AdminPolicyRuleTest {

  private final AdminPolicyRule rule = new AdminPolicyRule();

  @Test
  void shouldSupportAllActions() {
    assertThat(rule.supports("anything")).isTrue();
    assertThat(rule.supports("user:edit")).isTrue();
  }

  @Test
  void shouldAllowAdmin() {
    IcosUserPrincipal admin = principalWithRole("ADMIN");

    PolicyResult result = rule.check(admin, "user:edit", null);

    assertThat(result.effect()).isEqualTo(PolicyResult.Effect.ALLOW);
  }

  @Test
  void shouldAbstainForNonAdmin() {
    IcosUserPrincipal owner = principalWithRole("OWNER");

    PolicyResult result = rule.check(owner, "user:edit", null);

    assertThat(result.effect()).isEqualTo(PolicyResult.Effect.ABSTAIN);
  }

  @Test
  void shouldHaveHighestPriority() {
    assertThat(rule.getOrder()).isEqualTo(0);
  }

  private IcosUserPrincipal principalWithRole(String role) {
    return IcosUserPrincipal.builder()
            .userId(UUID.randomUUID())
            .email("test@test.com")
            .roleName(role)
            .authorities(List.of())
            .build();
  }
}