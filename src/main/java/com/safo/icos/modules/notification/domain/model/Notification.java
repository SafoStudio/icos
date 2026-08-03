package com.safo.icos.modules.notification.domain.model;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

import lombok.Builder;
import lombok.Getter;

/**
 * Domain entity representing a user notification.
 * Supports marking as read via {@link #markAsRead()}.
 */
@Getter
@Builder(toBuilder = true)
public class Notification {
  private final UUID id;
  private final UUID userId;
  private final NotificationType type;
  private final String title;
  private final String message;
  private final Map<String, String> metadata;
  private final boolean isRead;
  private final LocalDateTime readAt;
  private final LocalDateTime createdAt;

  /**
   * Returns a copy of this notification marked as read.
   *
   * @return a new {@code Notification} with {@code isRead = true} and current timestamp
   */
  public Notification markAsRead() {
    return this.toBuilder()
            .isRead(true)
            .readAt(LocalDateTime.now())
            .build();
  }
}