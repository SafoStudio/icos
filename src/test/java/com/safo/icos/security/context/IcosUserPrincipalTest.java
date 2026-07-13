package com.safo.icos.security.context;

import org.junit.jupiter.api.Test;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class IcosUserPrincipalTest {

  private static final UUID USER_ID = UUID.randomUUID();
  private static final UUID COMPANY_ID = UUID.randomUUID();
  private static final UUID MEMBERSHIP_ID = UUID.randomUUID();
  private static final String EMAIL = "user@test.com";

  @Test
  void shouldBuildPrincipal() {
    IcosUserPrincipal principal = IcosUserPrincipal.builder()
            .userId(USER_ID)
            .email(EMAIL)
            .membershipId(MEMBERSHIP_ID)
            .companyId(COMPANY_ID)
            .roleName("ADMIN")
            .departmentId(UUID.randomUUID())
            .departmentName("Engineering")
            .authorities(List.of(new SimpleGrantedAuthority("user:read")))
            .build();

    assertThat(principal.getUserId()).isEqualTo(USER_ID);
    assertThat(principal.getUsername()).isEqualTo(EMAIL);
    assertThat(principal.getEmail()).isEqualTo(EMAIL);
    assertThat(principal.getMembershipId()).isEqualTo(MEMBERSHIP_ID);
    assertThat(principal.getCompanyId()).isEqualTo(COMPANY_ID);
    assertThat(principal.getRoleName()).isEqualTo("ADMIN");
    assertThat(principal.getDepartmentName()).isEqualTo("Engineering");
    assertThat(principal.getAuthorities()).hasSize(1);
  }

  @Test
  void shouldAlwaysReturnTrueForAccountStatus() {
    IcosUserPrincipal principal = IcosUserPrincipal.builder()
            .userId(USER_ID).email(EMAIL).authorities(List.of()).build();

    assertThat(principal.isAccountNonExpired()).isTrue();
    assertThat(principal.isAccountNonLocked()).isTrue();
    assertThat(principal.isCredentialsNonExpired()).isTrue();
    assertThat(principal.isEnabled()).isTrue();
  }

  @Test
  void shouldReturnNullPassword() {
    IcosUserPrincipal principal = IcosUserPrincipal.builder()
            .userId(USER_ID).email(EMAIL).authorities(List.of()).build();

    assertThat(principal.getPassword()).isNull();
  }
}