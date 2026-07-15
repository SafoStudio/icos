package com.safo.icos.security.permission;

import com.safo.icos.security.context.IcosUserPrincipal;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PermissionsServiceTest {

  @Mock private PolicyRule rule1;
  @Mock private PolicyRule rule2;

  @InjectMocks
  private PermissionsService permissionsService;

  private final IcosUserPrincipal user = IcosUserPrincipal.builder()
          .userId(UUID.randomUUID())
          .email("test@test.com")
          .authorities(List.of())
          .build();

  @Test
  void shouldAllowWhenRuleReturnsAllow() {
    when(rule1.supports(anyString())).thenReturn(true);
    when(rule1.check(any(), anyString(), any())).thenReturn(PolicyResult.allow());

    permissionsService = new PermissionsService(List.of(rule1));

    assertThatCode(() -> permissionsService.assertCan(user, "user:edit", null))
            .doesNotThrowAnyException();
  }

  @Test
  void shouldDenyWhenRuleReturnsDeny() {
    when(rule1.supports(anyString())).thenReturn(true);
    when(rule1.check(any(), anyString(), any())).thenReturn(PolicyResult.deny("No access"));

    permissionsService = new PermissionsService(List.of(rule1));

    assertThatThrownBy(() -> permissionsService.assertCan(user, "user:edit", null))
            .isInstanceOf(AccessDeniedException.class)
            .hasMessageContaining("No access");
  }

  @Test
  void shouldSkipAbstainAndUseNextRule() {
    when(rule1.supports(anyString())).thenReturn(true);
    when(rule1.check(any(), anyString(), any())).thenReturn(PolicyResult.abstain());
    when(rule2.supports(anyString())).thenReturn(true);
    when(rule2.check(any(), anyString(), any())).thenReturn(PolicyResult.allow());

    permissionsService = new PermissionsService(List.of(rule1, rule2));

    assertThatCode(() -> permissionsService.assertCan(user, "user:edit", null))
            .doesNotThrowAnyException();
  }

  @Test
  void shouldDenyWhenNoRuleMatches() {
    when(rule1.supports(anyString())).thenReturn(false);

    permissionsService = new PermissionsService(List.of(rule1));

    assertThatThrownBy(() -> permissionsService.assertCan(user, "user:edit", null))
            .isInstanceOf(AccessDeniedException.class)
            .hasMessageContaining("No rule allowed");
  }

  @Test
  void shouldDenyWhenAllRulesAbstain() {
    when(rule1.supports(anyString())).thenReturn(true);
    when(rule1.check(any(), anyString(), any())).thenReturn(PolicyResult.abstain());

    permissionsService = new PermissionsService(List.of(rule1));

    assertThatThrownBy(() -> permissionsService.assertCan(user, "user:edit", null))
            .isInstanceOf(AccessDeniedException.class)
            .hasMessageContaining("No rule allowed");
  }
}
