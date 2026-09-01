package com.safo.icos.modules.talent.offboarding.infrastructure.repository.jpa;

import com.safo.icos.modules.talent.offboarding.infrastructure.repository.entity.OffboardingProcessJpaEntity;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OffboardingProcessJpaRepository extends JpaRepository<OffboardingProcessJpaEntity, UUID> {
  Optional<OffboardingProcessJpaEntity> findByMembershipId(UUID membershipId);
  List<OffboardingProcessJpaEntity> findByCompanyId(UUID companyId);
}