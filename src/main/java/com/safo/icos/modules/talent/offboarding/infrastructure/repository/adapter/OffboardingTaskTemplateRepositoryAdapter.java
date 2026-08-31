package com.safo.icos.modules.talent.offboarding.infrastructure.repository.adapter;

import com.safo.icos.modules.talent.offboarding.domain.model.OffboardingTaskTemplate;
import com.safo.icos.modules.talent.offboarding.domain.repository.OffboardingTaskTemplateRepository;
import com.safo.icos.modules.talent.offboarding.infrastructure.mapper.OffboardingMapper;
import com.safo.icos.modules.talent.offboarding.infrastructure.repository.jpa.OffboardingTaskTemplateJpaRepository;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
class OffboardingTaskTemplateRepositoryAdapter implements OffboardingTaskTemplateRepository {
  private final OffboardingTaskTemplateJpaRepository jpaRepository;
  private final OffboardingMapper mapper;

  @Override
  public OffboardingTaskTemplate save(OffboardingTaskTemplate taskTemplate) {
    return mapper.toDomain(jpaRepository.save(mapper.toEntity(taskTemplate)));
  }

  @Override
  public List<OffboardingTaskTemplate> findByTemplateId(UUID templateId) {
    return jpaRepository.findByTemplateId(templateId).stream().map(mapper::toDomain).toList();
  }
}