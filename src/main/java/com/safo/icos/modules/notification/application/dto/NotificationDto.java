package com.safo.icos.modules.notification.application.dto;

import com.safo.icos.modules.notification.domain.model.NotificationType;
import lombok.Builder;
import lombok.Getter;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Getter
@Builder
public class NotificationDto {
    private final UUID id;
    private final UUID userId;
    private final NotificationType type;
    private final String title;
    private final String message;
    private final Map<String, String> metadata;
    private final boolean isRead;
    private final LocalDateTime readAt;
    private final LocalDateTime createdAt;
}
