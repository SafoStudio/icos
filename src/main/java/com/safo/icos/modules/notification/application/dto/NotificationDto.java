package com.safo.icos.modules.notification.application.dto;

import com.safo.icos.modules.notification.domain.model.NotificationType;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

import lombok.Builder;
import lombok.Getter;

/**
 * DTO representing a user notification.
 */
@Getter
@Builder
public class NotificationDto {
  private final UUID id;
  private final UUID userId;
  private final NotificationType type;
  private final String title;
  private final String message;
  /** Optional metadata (e.g., related entity IDs). */
  private final Map<String, String> metadata;
  private final boolean isRead;
  private final LocalDateTime readAt;
  private final LocalDateTime createdAt;
}