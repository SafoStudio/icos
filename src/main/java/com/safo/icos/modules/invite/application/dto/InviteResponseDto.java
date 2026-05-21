package com.safo.icos.modules.invite.application.dto;

import com.safo.icos.common.enums.InviteStatus;
import com.safo.icos.common.enums.RoleName;
import java.time.OffsetDateTime;
import java.util.UUID;

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
