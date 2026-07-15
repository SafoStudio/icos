package com.safo.icos.modules.identity.user.domain.model;

import java.time.LocalDateTime;
import java.util.UUID;

import lombok.Builder;
import lombok.Getter;

/**
 * Domain entity representing a user's membership in a company.
 * Links a user to a company with a specific role and optional department.
 * Can be deactivated or have its role changed.
 */
@Getter
@Builder
public class Membership {
  private final UUID id;
  private final UUID userId;
  private final UUID companyId;
  private final UUID departmentId;
  private final UUID managerId;
  private final LocalDateTime createdAt;
  private UUID roleId;
  private boolean active;
  private LocalDateTime updatedAt;

  /**
   * Factory method to create a new active membership.
   *
   * @param userId       the user joining the company
   * @param companyId    the target company
   * @param roleId       the assigned role
   * @param departmentId optional department assignment
   * @param managerId    optional manager assignment
   * @return a newly created active {@code Membership}
   */
  public static Membership create(UUID userId, UUID companyId, UUID roleId,
                                  UUID departmentId, UUID managerId) {
    return Membership.builder()
            .id(UUID.randomUUID())
            .userId(userId)
            .companyId(companyId)
            .roleId(roleId)
            .departmentId(departmentId)
            .managerId(managerId)
            .active(true)
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build();
  }

  /** Marks this membership as inactive. */
  public void deactivate() {
    this.active = false;
    this.updatedAt = LocalDateTime.now();
  }

  /** Changes the role assigned to this membership. */
  public void changeRole(UUID newRoleId) {
    this.roleId = newRoleId;
    this.updatedAt = LocalDateTime.now();
  }
}
