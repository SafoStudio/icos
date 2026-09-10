package com.safo.icos.modules.notification.infrastructure.repository.adapter;

import com.safo.icos.modules.notification.domain.model.NotificationPreference;
import com.safo.icos.modules.notification.domain.model.NotificationType;
import com.safo.icos.modules.notification.domain.repository.NotificationPreferenceRepository;
import com.safo.icos.modules.notification.infrastructure.mapper.NotificationPreferenceMapper;
import com.safo.icos.modules.notification.infrastructure.repository.entity.NotificationPreferenceEntity;
import com.safo.icos.modules.notification.infrastructure.repository.jpa.NotificationPreferenceJpaRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class NotificationPreferenceRepositoryAdapter implements NotificationPreferenceRepository {

  private final NotificationPreferenceJpaRepository jpaRepository;
  private final NotificationPreferenceMapper mapper;

  @Override
  public NotificationPreference save(NotificationPreference preference) {
    NotificationPreferenceEntity entity = mapper.toEntity(preference);
    return mapper.toDomain(jpaRepository.save(entity));
  }

  @Override
  public List<NotificationPreference> saveAll(List<NotificationPreference> preferences) {
    List<NotificationPreferenceEntity> entities = preferences.stream()
            .map(mapper::toEntity)
            .toList();
    return jpaRepository.saveAll(entities).stream()
            .map(mapper::toDomain)
            .toList();
  }

  @Override
  public Optional<NotificationPreference> findByUserIdAndType(UUID userId, NotificationType type) {
    return jpaRepository.findByUserIdAndType(userId, type).map(mapper::toDomain);
  }

  @Override
  public List<NotificationPreference> findByUserId(UUID userId) {
    return jpaRepository.findByUserId(userId).stream()
            .map(mapper::toDomain)
            .toList();
  }
}
