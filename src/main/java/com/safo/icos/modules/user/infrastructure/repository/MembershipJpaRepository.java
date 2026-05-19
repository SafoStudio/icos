package com.safo.icos.modules.user.infrastructure.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MembershipJpaRepository extends JpaRepository<MembershipJpaEntity, UUID> {
    
    @Query("SELECT m FROM MembershipJpaEntity m WHERE m.userId = :userId AND m.companyId = :companyId AND m.active = true")
    Optional<MembershipJpaEntity> findActiveByUserIdAndCompanyId(UUID userId, UUID companyId);

    List<MembershipJpaEntity> findByUserId(UUID userId);
}
