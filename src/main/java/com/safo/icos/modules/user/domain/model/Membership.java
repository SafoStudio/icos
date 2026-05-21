package com.safo.icos.modules.user.domain.model;

import lombok.Builder;
import lombok.Getter;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
public class Membership {
    private final UUID id;
    private final UUID userId;
    private final UUID companyId;
    private UUID roleId;
    private final UUID departmentId;
    private final UUID managerId;
    private boolean active;
    private final LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static Membership create(UUID userId, UUID companyId, UUID roleId, UUID departmentId, UUID managerId) {
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

    public void deactivate() {
        this.active = false;
        this.updatedAt = LocalDateTime.now();
    }

    public void changeRole(UUID newRoleId) {
        this.roleId = newRoleId;
        this.updatedAt = LocalDateTime.now();
    }
}
