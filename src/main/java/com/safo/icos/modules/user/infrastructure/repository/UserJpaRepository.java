package com.safo.icos.modules.user.infrastructure.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import com.safo.icos.modules.user.domain.model.UserStatus;

public interface UserJpaRepository extends JpaRepository<UserJpaEntity, UUID> {

    Optional<UserJpaEntity> findByEmailAndCompanyId(String email, UUID companyId);

    List<UserJpaEntity> findByCompanyId(UUID companyId);

    List<UserJpaEntity> findByCompanyIdAndStatus(UUID companyId, UserStatus status);

    boolean existsByEmailAndCompanyId(String email, UUID companyId);

    @Query("SELECT u FROM UserJpaEntity u WHERE u.companyId = :companyId AND u.status = :status ORDER BY u.createdAt DESC")
    List<UserJpaEntity> findActiveByCompanyId(@Param("companyId") UUID companyId, @Param("status") UserStatus status);
}