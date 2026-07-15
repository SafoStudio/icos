package com.safo.icos.security.permission.rule;

import com.safo.icos.security.context.IcosUserPrincipal;
import com.safo.icos.security.permission.PolicyResult;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class ProfilePolicyRuleTest {

  private final ProfilePolicyRule rule = new ProfilePolicyRule();
  private static final UUID USER_ID = UUID.randomUUID();
  private static final UUID OTHER_USER_ID = UUID.randomUUID();

  @Test
  void shouldOnlySupportProfileEditAction() {
    assertThat(rule.supports("profile:edit")).isTrue();
    assertThat(rule.supports("profile:read")).isFalse();
  }

  @Test
  void shouldAllowEditingOwnProfile() {
    IcosUserPrincipal user = principalWithAuthorities(USER_ID, "SPECIALIST", "HR",
            List.of(new SimpleGrantedAuthority("profile:edit.self")));

    PolicyResult result = rule.check(user, "profile:edit", USER_ID);

    assertThat(result.effect()).isEqualTo(PolicyResult.Effect.ALLOW);
  }

  @Test
  void shouldAllowUserWithFullProfilePermission() {
    IcosUserPrincipal user = principalWithAuthorities(USER_ID, "ADMIN", null,
            List.of(new SimpleGrantedAuthority("profile:edit.all")));

    PolicyResult result = rule.check(user, "profile:edit", OTHER_USER_ID);

    assertThat(result.effect()).isEqualTo(PolicyResult.Effect.ALLOW);
  }

  @Test
  void shouldAllowHrHead() {
    IcosUserPrincipal hrHead = principalWithAuthorities(USER_ID, "HEAD", "HR", List.of());

    PolicyResult result = rule.check(hrHead, "profile:edit", OTHER_USER_ID);

    assertThat(result.effect()).isEqualTo(PolicyResult.Effect.ALLOW);
  }

  @Test
  void shouldDenyEditingOthersProfileWithoutPermission() {
    IcosUserPrincipal specialist = principalWithAuthorities(USER_ID, "SPECIALIST", "Engineering",
            List.of(new SimpleGrantedAuthority("profile:edit.self")));

    PolicyResult result = rule.check(specialist, "profile:edit", OTHER_USER_ID);

    assertThat(result.effect()).isEqualTo(PolicyResult.Effect.DENY);
  }

  @Test
  void shouldAbstainWhenResourceIsNotUuid() {
    IcosUserPrincipal user = principalWithAuthorities(USER_ID, "ADMIN", null, List.of());

    PolicyResult result = rule.check(user, "profile:edit", "not-a-uuid");

    assertThat(result.effect()).isEqualTo(PolicyResult.Effect.ABSTAIN);
  }

  private IcosUserPrincipal principalWithAuthorities(UUID userId, String role,
                                                     String departmentName,
                                                     List<SimpleGrantedAuthority> authorities) {
    return IcosUserPrincipal.builder()
            .userId(userId).email("test@test.com")
            .roleName(role).departmentName(departmentName)
            .authorities(authorities).build();
  }
}
