package com.safo.icos.modules.identity.invite.application.dto;

import com.safo.icos.modules.identity.invite.domain.model.enums.InviteStatus;
import com.safo.icos.modules.org.domain.model.enums.RoleName;
import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Response returned after creating or retrieving an invitation.
 * Contains full invite details with resolved names for display.
 */
public record InviteResponseDto(
        UUID id,
        String email,
        RoleName role,
        UUID companyId,
        String companyName,
        UUID departmentId,
        String departmentName,
        UUID managerId,
        String managerName,
        InviteStatus status,
        OffsetDateTime expiresAt
) {
}
