package com.safo.icos.security.permission.rule;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.safo.icos.security.context.IcosUserPrincipal;
import com.safo.icos.security.permission.PolicyResult;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

class CustomFieldPolicyRuleTest {

  private CustomFieldPolicyRule rule;

  @BeforeEach
  void setUp() {
    rule = new CustomFieldPolicyRule();
  }

  @Test
  @DisplayName("Should support customfield:* action strings")
  void shouldSupportCustomFieldActions() {
    assertTrue(rule.supports("customfield:read"));
    assertTrue(rule.supports("customfield:manage"));
  }

  @Test
  @DisplayName("Should allow customfield:read for any authenticated user")
  void shouldAllowReadForAuthenticatedUser() {
    IcosUserPrincipal user = mock(IcosUserPrincipal.class);
    when(user.getRoleName()).thenReturn("SPECIALIST");
    when(user.getDepartmentName()).thenReturn("Engineering");
    when(user.getAuthorities()).thenReturn(Collections.emptyList());

    PolicyResult result = rule.check(user, "customfield:read", null);
    assertEquals(PolicyResult.Effect.ALLOW, result.effect());
  }

  @Test
  @DisplayName("Should allow customfield:manage for ADMIN or OWNER")
  void shouldAllowManageForAdminOrOwner() {
    IcosUserPrincipal owner = mock(IcosUserPrincipal.class);
    when(owner.getRoleName()).thenReturn("OWNER");
    when(owner.getDepartmentName()).thenReturn("Management");
    when(owner.getAuthorities()).thenReturn(Collections.emptyList());

    PolicyResult result = rule.check(owner, "customfield:manage", null);
    assertEquals(PolicyResult.Effect.ALLOW, result.effect());
  }

  @Test
  @DisplayName("Should allow customfield:manage for ADMIN")
  void shouldAllowManageForAdmin() {
    IcosUserPrincipal admin = mock(IcosUserPrincipal.class);
    when(admin.getRoleName()).thenReturn("ADMIN");
    when(admin.getDepartmentName()).thenReturn("IT");
    when(admin.getAuthorities()).thenReturn(Collections.emptyList());

    PolicyResult result = rule.check(admin, "customfield:manage", null);
    assertEquals(PolicyResult.Effect.ALLOW, result.effect());
  }

  @Test
  @DisplayName("Should allow customfield:manage for HR department")
  void shouldAllowManageForHrDepartment() {
    IcosUserPrincipal hr = mock(IcosUserPrincipal.class);
    when(hr.getRoleName()).thenReturn("SPECIALIST");
    when(hr.getDepartmentName()).thenReturn("HR Department");
    when(hr.getAuthorities()).thenReturn(Collections.emptyList());

    PolicyResult result = rule.check(hr, "customfield:manage", null);
    assertEquals(PolicyResult.Effect.ALLOW, result.effect());
  }

  @Test
  @DisplayName("Should deny customfield:manage for non-HR specialist")
  void shouldDenyManageForNonHrSpecialist() {
    IcosUserPrincipal specialist = mock(IcosUserPrincipal.class);
    when(specialist.getRoleName()).thenReturn("SPECIALIST");
    when(specialist.getDepartmentName()).thenReturn("Engineering");
    when(specialist.getAuthorities()).thenReturn(Collections.emptyList());

    PolicyResult result = rule.check(specialist, "customfield:manage", null);
    assertEquals(PolicyResult.Effect.DENY, result.effect());
  }

  @Test
  @DisplayName("Should allow customfield:manage with direct granted authority")
  void shouldAllowManageWithDirectAuthority() {
    IcosUserPrincipal user = mock(IcosUserPrincipal.class);
    when(user.getRoleName()).thenReturn("SPECIALIST");
    when(user.getDepartmentName()).thenReturn("Engineering");

    Collection<GrantedAuthority> authorities = new ArrayList<>();
    authorities.add(new SimpleGrantedAuthority("customfield:manage"));

    doReturn(authorities).when(user).getAuthorities();

    PolicyResult result = rule.check(user, "customfield:manage", null);
    assertEquals(PolicyResult.Effect.ALLOW, result.effect());
  }

  @Test
  @DisplayName("Should deny for unauthenticated user")
  void shouldDenyForUnauthenticatedUser() {
    PolicyResult result = rule.check(null, "customfield:read", null);
    assertEquals(PolicyResult.Effect.DENY, result.effect());
  }
}