package com.safo.icos.modules.identity.invite.application.dto;

import com.safo.icos.modules.org.domain.model.enums.RoleName;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

/**
 * Request payload for creating a new invitation.
 */
public record InviteRequestDto(
        @Email @NotBlank String email,
        @NotNull RoleName role,
        @NotNull UUID departmentId,
        UUID managerId
) {
}
