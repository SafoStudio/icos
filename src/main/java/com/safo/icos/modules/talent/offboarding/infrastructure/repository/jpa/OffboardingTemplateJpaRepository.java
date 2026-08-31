package com.safo.icos.modules.talent.offboarding.infrastructure.repository.jpa;

import com.safo.icos.modules.talent.offboarding.infrastructure.repository.entity.OffboardingTemplateJpaEntity;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OffboardingTemplateJpaRepository extends JpaRepository<OffboardingTemplateJpaEntity, UUID> {
  List<OffboardingTemplateJpaEntity> findByCompanyId(UUID companyId);
}