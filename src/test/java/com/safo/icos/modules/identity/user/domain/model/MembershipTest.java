package com.safo.icos.modules.identity.user.domain.model;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class MembershipTest {

  private static final UUID USER_ID = UUID.randomUUID();
  private static final UUID COMPANY_ID = UUID.randomUUID();
  private static final UUID ROLE_ID = UUID.randomUUID();
  private static final UUID DEPARTMENT_ID = UUID.randomUUID();
  private static final UUID MANAGER_ID = UUID.randomUUID();

  @Test
  void shouldCreateActiveMembership() {
    Membership membership = Membership.create(USER_ID, COMPANY_ID, ROLE_ID, DEPARTMENT_ID, MANAGER_ID);

    assertThat(membership.getId()).isNotNull();
    assertThat(membership.getUserId()).isEqualTo(USER_ID);
    assertThat(membership.getCompanyId()).isEqualTo(COMPANY_ID);
    assertThat(membership.getRoleId()).isEqualTo(ROLE_ID);
    assertThat(membership.getDepartmentId()).isEqualTo(DEPARTMENT_ID);
    assertThat(membership.getManagerId()).isEqualTo(MANAGER_ID);
    assertThat(membership.isActive()).isTrue();
    assertThat(membership.getCreatedAt()).isNotNull();
  }

  @Test
  void shouldDeactivateMembership() {
    Membership membership = Membership.create(USER_ID, COMPANY_ID, ROLE_ID, null, null);

    membership.deactivate();

    assertThat(membership.isActive()).isFalse();
  }

  @Test
  void shouldChangeRole() {
    Membership membership = Membership.create(USER_ID, COMPANY_ID, ROLE_ID, null, null);
    UUID newRoleId = UUID.randomUUID();

    membership.changeRole(newRoleId);

    assertThat(membership.getRoleId()).isEqualTo(newRoleId);
  }

  @Test
  void shouldAllowNullDepartmentAndManager() {
    Membership membership = Membership.create(USER_ID, COMPANY_ID, ROLE_ID, null, null);

    assertThat(membership.getDepartmentId()).isNull();
    assertThat(membership.getManagerId()).isNull();
  }
}
