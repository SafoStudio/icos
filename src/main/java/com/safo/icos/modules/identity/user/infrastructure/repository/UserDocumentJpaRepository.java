package com.safo.icos.modules.user.infrastructure.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserDocumentJpaRepository extends JpaRepository<UserDocumentJpaEntity, UUID> {
    List<UserDocumentJpaEntity> findAllByUserId(UUID userId);
    Optional<UserDocumentJpaEntity> findByUserIdAndTypeAndFilename(UUID userId, String type, String filename);
    void deleteByUserIdAndTypeAndFilename(UUID userId, String type, String filename);
}
