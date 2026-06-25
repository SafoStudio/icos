package com.safo.icos.modules.identity.user.application.dto;

import com.safo.icos.modules.identity.user.domain.model.enums.UserStatus;
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
  private final ProfileResponseDto profile;
  private final java.util.List<DocumentResponseDto> documents;
}
