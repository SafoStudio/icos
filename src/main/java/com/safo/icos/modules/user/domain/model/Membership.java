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
    private String role;
    private final UUID departmentId;
    private boolean active;
    private final LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static Membership create(UUID userId, UUID companyId, String role, UUID departmentId) {
        return Membership.builder()
                .id(UUID.randomUUID())
                .userId(userId)
                .companyId(companyId)
                .role(role)
                .departmentId(departmentId)
                .active(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    public void deactivate() {
        this.active = false;
        this.updatedAt = LocalDateTime.now();
    }

    public void changeRole(String newRole) {
        this.role = newRole;
        this.updatedAt = LocalDateTime.now();
    }
}