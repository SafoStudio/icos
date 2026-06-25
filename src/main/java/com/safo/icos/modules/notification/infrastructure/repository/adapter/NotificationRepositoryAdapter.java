package com.safo.icos.modules.notification.infrastructure.repository.adapter;

import com.safo.icos.modules.notification.domain.model.Notification;
import com.safo.icos.modules.notification.domain.repository.NotificationRepository;
import com.safo.icos.modules.notification.infrastructure.mapper.NotificationMapper;
import com.safo.icos.modules.notification.infrastructure.repository.jpa.NotificationJpaRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@RequiredArgsConstructor
public class NotificationRepositoryAdapter implements NotificationRepository {

    private final NotificationJpaRepository jpaRepository;
    private final NotificationMapper mapper;

    @Override
    public Notification save(Notification notification) {
        return mapper.toDomain(jpaRepository.save(mapper.toEntity(notification)));
    }

    @Override
    public Optional<Notification> findById(UUID id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<Notification> findByUserId(UUID userId, int limit, int offset) {
        return jpaRepository.findByUserIdOrderByCreatedAtDesc(userId, PageRequest.of(offset / limit, limit))
                .stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public long countUnreadByUserId(UUID userId) {
        return jpaRepository.countByUserIdAndIsReadFalse(userId);
    }

    @Override
    @Transactional
    public void markAllAsReadByUserId(UUID userId) {
        jpaRepository.markAllAsReadByUserId(userId);
    }
}
