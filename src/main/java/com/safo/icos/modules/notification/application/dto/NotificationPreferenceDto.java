package com.safo.icos.modules.notification.application.dto;

import com.safo.icos.modules.notification.domain.model.NotificationType;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationPreferenceDto {
  private UUID id;
  private UUID userId;
  private NotificationType type;
  private boolean inAppEnabled;
  private boolean emailEnabled;
}
