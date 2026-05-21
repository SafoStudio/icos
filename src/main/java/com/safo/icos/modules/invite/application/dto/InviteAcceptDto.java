package com.safo.icos.modules.invite.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record InviteAcceptDto(
    @NotBlank String firstName,
    @NotBlank String lastName,
    @NotBlank @Size(min = 8) String password
) {}
