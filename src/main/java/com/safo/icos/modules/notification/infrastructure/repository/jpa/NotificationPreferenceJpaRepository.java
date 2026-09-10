package com.safo.icos.modules.notification.infrastructure.repository.jpa;

import com.safo.icos.modules.notification.domain.model.NotificationType;
import com.safo.icos.modules.notification.infrastructure.repository.entity.NotificationPreferenceEntity;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationPreferenceJpaRepository extends JpaRepository<NotificationPreferenceEntity, UUID> {
  Optional<NotificationPreferenceEntity> findByUserIdAndType(UUID userId, NotificationType type);
  List<NotificationPreferenceEntity> findByUserId(UUID userId);
}
