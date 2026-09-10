package com.safo.icos.modules.notification.application;

import com.safo.icos.modules.notification.application.dto.NotificationDto;
import com.safo.icos.modules.notification.domain.model.NotificationType;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * High-level notification dispatcher for sending notifications across different modules.
 * Ensures notifications respect user preferences and broadcast to real-time streams.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationDispatcher {

  private final NotificationService notificationService;

  public NotificationDto dispatch(UUID recipientId, NotificationType type, String title, String message) {
    return dispatch(recipientId, type, title, message, Collections.emptyMap());
  }

  public NotificationDto dispatch(UUID recipientId, NotificationType type, String title, String message,
                                  Map<String, String> metadata) {
    if (recipientId == null) {
      log.warn("Cannot dispatch notification of type {}: recipientId is null", type);
      return null;
    }
    return notificationService.createNotification(recipientId, type, title, message, metadata);
  }

  public List<NotificationDto> dispatchToMany(Collection<UUID> recipientIds, NotificationType type,
                                              String title, String message,
                                              Map<String, String> metadata) {
    if (recipientIds == null || recipientIds.isEmpty()) {
      return Collections.emptyList();
    }
    return recipientIds.stream()
            .filter(Objects::nonNull)
            .distinct()
            .map(id -> dispatch(id, type, title, message, metadata))
            .filter(Objects::nonNull)
            .toList();
  }
}
