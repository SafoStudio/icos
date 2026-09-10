package com.safo.icos.modules.notification.domain.model;

import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Builder;
import lombok.Getter;

/**
 * Domain entity representing a user's notification preference for a specific notification type.
 */
@Getter
@Builder(toBuilder = true)
public class NotificationPreference {
  private final UUID id;
  private final UUID userId;
  private final NotificationType type;
  private final boolean inAppEnabled;
  private final boolean emailEnabled;
  private final LocalDateTime createdAt;
  private final LocalDateTime updatedAt;

  public NotificationPreference update(boolean inAppEnabled, boolean emailEnabled) {
    return this.toBuilder()
            .inAppEnabled(inAppEnabled)
            .emailEnabled(emailEnabled)
            .updatedAt(LocalDateTime.now())
            .build();
  }
}
