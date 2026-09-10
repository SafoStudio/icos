package com.safo.icos.modules.notification.infrastructure.mapper;

import com.safo.icos.modules.notification.domain.model.NotificationPreference;
import com.safo.icos.modules.notification.infrastructure.repository.entity.NotificationPreferenceEntity;
import org.springframework.stereotype.Component;

@Component
public class NotificationPreferenceMapper {

  public NotificationPreference toDomain(NotificationPreferenceEntity entity) {
    if (entity == null) return null;
    return NotificationPreference.builder()
            .id(entity.getId())
            .userId(entity.getUserId())
            .type(entity.getType())
            .inAppEnabled(entity.isInAppEnabled())
            .emailEnabled(entity.isEmailEnabled())
            .createdAt(entity.getCreatedAt())
            .updatedAt(entity.getUpdatedAt())
            .build();
  }

  public NotificationPreferenceEntity toEntity(NotificationPreference domain) {
    if (domain == null) return null;
    return NotificationPreferenceEntity.builder()
            .id(domain.getId())
            .userId(domain.getUserId())
            .type(domain.getType())
            .inAppEnabled(domain.isInAppEnabled())
            .emailEnabled(domain.isEmailEnabled())
            .createdAt(domain.getCreatedAt())
            .updatedAt(domain.getUpdatedAt())
            .build();
  }
}
