package com.safo.icos.modules.identity.user.infrastructure.repository.jpa;

import com.safo.icos.modules.identity.user.infrastructure.repository.entity.UserDocumentJpaEntity;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserDocumentJpaRepository extends JpaRepository<UserDocumentJpaEntity, UUID> {
    List<UserDocumentJpaEntity> findAllByUserId(UUID userId);
    Optional<UserDocumentJpaEntity> findByUserIdAndTypeAndFilename(UUID userId, String type, String filename);
    void deleteByUserIdAndTypeAndFilename(UUID userId, String type, String filename);
}
