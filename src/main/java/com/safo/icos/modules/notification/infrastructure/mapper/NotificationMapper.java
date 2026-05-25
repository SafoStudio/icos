package com.safo.icos.modules.notification.infrastructure.mapper;

import com.safo.icos.modules.notification.domain.model.Notification;
import com.safo.icos.modules.notification.infrastructure.repository.entity.NotificationEntity;
import org.springframework.stereotype.Component;

@Component
public class NotificationMapper {

    public Notification toDomain(NotificationEntity entity) {
        if (entity == null) return null;
        return Notification.builder()
                .id(entity.getId())
                .userId(entity.getUserId())
                .type(entity.getType())
                .title(entity.getTitle())
                .message(entity.getMessage())
                .metadata(entity.getMetadata())
                .isRead(entity.isRead())
                .readAt(entity.getReadAt())
                .createdAt(entity.getCreatedAt())
                .build();
    }

    public NotificationEntity toEntity(Notification domain) {
        if (domain == null) return null;
        return NotificationEntity.builder()
                .id(domain.getId())
                .userId(domain.getUserId())
                .type(domain.getType())
                .title(domain.getTitle())
                .message(domain.getMessage())
                .metadata(domain.getMetadata())
                .isRead(domain.isRead())
                .readAt(domain.getReadAt())
                .createdAt(domain.getCreatedAt())
                .build();
    }
}
