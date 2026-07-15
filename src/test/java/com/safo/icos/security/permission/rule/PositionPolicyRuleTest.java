package com.safo.icos.security.permission.rule;

import com.safo.icos.security.context.IcosUserPrincipal;
import com.safo.icos.security.permission.PolicyResult;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class PositionPolicyRuleTest {

  private final PositionPolicyRule rule = new PositionPolicyRule();

  @Test
  void shouldSupportPositionActions() {
    assertThat(rule.supports("position:create")).isTrue();
    assertThat(rule.supports("position:manage")).isTrue();
    assertThat(rule.supports("position:delete")).isTrue();
  }

  @Test
  void shouldNotSupportOtherActions() {
    assertThat(rule.supports("department:create")).isFalse();
    assertThat(rule.supports("user:edit")).isFalse();
  }

  @Test
  void shouldAllowOwner() {
    IcosUserPrincipal owner = principalWithRole("OWNER");

    PolicyResult result = rule.check(owner, "position:create", null);

    assertThat(result.effect()).isEqualTo(PolicyResult.Effect.ALLOW);
  }

  @Test
  void shouldAbstainForAdmin() {
    IcosUserPrincipal admin = principalWithRole("ADMIN");

    PolicyResult result = rule.check(admin, "position:create", null);

    assertThat(result.effect()).isEqualTo(PolicyResult.Effect.ABSTAIN);
  }

  @Test
  void shouldAbstainForSpecialist() {
    IcosUserPrincipal specialist = principalWithRole("SPECIALIST");

    PolicyResult result = rule.check(specialist, "position:create", null);

    assertThat(result.effect()).isEqualTo(PolicyResult.Effect.ABSTAIN);
  }

  private IcosUserPrincipal principalWithRole(String role) {
    return IcosUserPrincipal.builder()
            .userId(UUID.randomUUID()).email("test@test.com")
            .roleName(role).authorities(List.of()).build();
  }
}
