package com.safo.icos.modules.invite.domain.model;

import com.safo.icos.common.enums.RoleName;
import com.safo.icos.common.enums.InviteStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
@Builder
public class Invite {
    private final UUID id;
    private final String email;
    private final RoleName role;
    private final UUID companyId;
    private final UUID departmentId;
    private final UUID managerId;
    private final UUID invitedById;
    private final String token;
    private InviteStatus status;
    private final OffsetDateTime expiresAt;
    private final OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;

    public boolean isExpired() {
        return OffsetDateTime.now().isAfter(expiresAt);
    }

    public boolean isPending() {
        return status == InviteStatus.PENDING;
    }

    public void accept() {
        if (!isPending()) {
            throw new IllegalStateException("Only pending invites can be accepted. Current status: " + status);
        }
        if (isExpired()) {
            this.status = InviteStatus.EXPIRED;
            throw new IllegalStateException("Invite has expired");
        }
        this.status = InviteStatus.ACCEPTED;
        this.updatedAt = OffsetDateTime.now();
    }

    public void cancel() {
        if (!isPending()) {
            throw new IllegalStateException("Only pending invites can be canceled");
        }
        this.status = InviteStatus.CANCELED;
        this.updatedAt = OffsetDateTime.now();
    }
}
