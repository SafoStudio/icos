package com.safo.icos.modules.user.infrastructure.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface MembershipJpaRepository extends JpaRepository<MembershipJpaEntity, UUID> {

  @Query("SELECT m FROM MembershipJpaEntity m " +
          "WHERE m.userId = :userId " +
          "AND m.companyId = :companyId " +
          "AND m.active = true")
  Optional<MembershipJpaEntity> findActiveByUserIdAndCompanyId(UUID userId, UUID companyId);

  List<MembershipJpaEntity> findAllByCompanyId(UUID companyId);

  Optional<MembershipJpaEntity> findFirstByUserIdAndActiveTrue(UUID userId);

  List<MembershipJpaEntity> findByUserId(UUID userId);

  Optional<MembershipJpaEntity> findFirstByDepartmentIdAndRoleIdAndActiveTrue(UUID departmentId, UUID roleId);
}
