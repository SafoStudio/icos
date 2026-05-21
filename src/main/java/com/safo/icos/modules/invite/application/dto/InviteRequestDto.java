package com.safo.icos.modules.invite.application.dto;

import com.safo.icos.common.enums.RoleName;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record InviteRequestDto(
        @Email @NotBlank String email,
        @NotNull RoleName role,
        @NotNull UUID departmentId,
        UUID managerId
) {
}
