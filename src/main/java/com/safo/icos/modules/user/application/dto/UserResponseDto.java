package com.safo.icos.modules.user.application.dto;

import com.safo.icos.modules.user.domain.model.UserStatus;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserResponseDto {
  private final UUID id;
  private final String firstName;
  private final String lastName;
  private final String email;
  private final UserStatus status;
  private final UUID companyId;
  private final UUID createdBy;
  private final LocalDateTime createdAt;
  private final LocalDateTime updatedAt;
}