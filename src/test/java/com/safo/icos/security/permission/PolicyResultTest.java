package com.safo.icos.security.permission;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PolicyResultTest {

  @Test
  void shouldCreateAllowWithoutReason() {
    PolicyResult result = PolicyResult.allow();

    assertThat(result.effect()).isEqualTo(PolicyResult.Effect.ALLOW);
    assertThat(result.reason()).isNull();
  }

  @Test
  void shouldCreateAllowWithReason() {
    PolicyResult result = PolicyResult.allow("Admin has full access");

    assertThat(result.effect()).isEqualTo(PolicyResult.Effect.ALLOW);
    assertThat(result.reason()).isEqualTo("Admin has full access");
  }

  @Test
  void shouldCreateDeny() {
    PolicyResult result = PolicyResult.deny("Access denied");

    assertThat(result.effect()).isEqualTo(PolicyResult.Effect.DENY);
    assertThat(result.reason()).isEqualTo("Access denied");
  }

  @Test
  void shouldCreateAbstain() {
    PolicyResult result = PolicyResult.abstain();

    assertThat(result.effect()).isEqualTo(PolicyResult.Effect.ABSTAIN);
    assertThat(result.reason()).isNull();
  }
}
