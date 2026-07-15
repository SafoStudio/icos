package com.safo.icos.modules.identity.invite.domain.model;

import com.safo.icos.modules.identity.invite.domain.model.enums.InviteStatus;
import com.safo.icos.modules.org.domain.model.enums.RoleName;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class InviteTest {

  @Test
  void shouldCreatePendingInvite() {
    Invite invite = Invite.builder()
            .id(UUID.randomUUID())
            .email("test@example.com")
            .role(RoleName.SPECIALIST)
            .companyId(UUID.randomUUID())
            .departmentId(UUID.randomUUID())
            .managerId(null)
            .invitedById(UUID.randomUUID())
            .token("hashed-token")
            .status(InviteStatus.PENDING)
            .expiresAt(OffsetDateTime.now().plusDays(7))
            .createdAt(OffsetDateTime.now())
            .build();

    assertThat(invite.isPending()).isTrue();
    assertThat(invite.isNotPending()).isFalse();
    assertThat(invite.isExpired()).isFalse();
  }

  @Test
  void shouldAcceptPendingInvite() {
    Invite invite = pendingInvite();

    invite.accept();

    assertThat(invite.getStatus()).isEqualTo(InviteStatus.ACCEPTED);
    assertThat(invite.isPending()).isFalse();
  }

  @Test
  void shouldThrowWhenAcceptingExpiredInvite() {
    Invite invite = Invite.builder()
            .id(UUID.randomUUID())
            .email("test@example.com")
            .role(RoleName.SPECIALIST)
            .companyId(UUID.randomUUID())
            .departmentId(UUID.randomUUID())
            .managerId(null)
            .invitedById(UUID.randomUUID())
            .token("hashed-token")
            .status(InviteStatus.PENDING)
            .expiresAt(OffsetDateTime.now().minusDays(1))
            .createdAt(OffsetDateTime.now().minusDays(8))
            .build();

    assertThatThrownBy(invite::accept)
            .isInstanceOf(IllegalStateException.class)
            .hasMessageContaining("expired");
  }

  @Test
  void shouldThrowWhenAcceptingAlreadyAcceptedInvite() {
    Invite invite = pendingInvite();
    invite.accept();

    assertThatThrownBy(invite::accept)
            .isInstanceOf(IllegalStateException.class)
            .hasMessageContaining("Only pending");
  }

  @Test
  void shouldCancelPendingInvite() {
    Invite invite = pendingInvite();

    invite.cancel();

    assertThat(invite.getStatus()).isEqualTo(InviteStatus.CANCELED);
  }

  @Test
  void shouldThrowWhenCancellingAlreadyAcceptedInvite() {
    Invite invite = pendingInvite();
    invite.accept();

    assertThatThrownBy(invite::cancel)
            .isInstanceOf(IllegalStateException.class)
            .hasMessageContaining("Only pending");
  }

  @Test
  void shouldDetectExpiredInvite() {
    Invite invite = Invite.builder()
            .status(InviteStatus.PENDING)
            .expiresAt(OffsetDateTime.now().minusDays(1))
            .build();

    assertThat(invite.isExpired()).isTrue();
  }

  @Test
  void shouldDetectNotExpiredInvite() {
    Invite invite = Invite.builder()
            .status(InviteStatus.PENDING)
            .expiresAt(OffsetDateTime.now().plusDays(1))
            .build();

    assertThat(invite.isExpired()).isFalse();
  }

  private Invite pendingInvite() {
    return Invite.builder()
            .id(UUID.randomUUID())
            .email("test@example.com")
            .role(RoleName.SPECIALIST)
            .companyId(UUID.randomUUID())
            .departmentId(UUID.randomUUID())
            .managerId(null)
            .invitedById(UUID.randomUUID())
            .token("hashed-token")
            .status(InviteStatus.PENDING)
            .expiresAt(OffsetDateTime.now().plusDays(7))
            .createdAt(OffsetDateTime.now())
            .build();
  }
}
