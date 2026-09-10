package com.safo.icos.modules.notification.application.dto;

import com.safo.icos.modules.notification.domain.model.NotificationType;

public record NotificationPreferenceInput(
    NotificationType type,
    boolean inAppEnabled,
    boolean emailEnabled
) {}
