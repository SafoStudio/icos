package com.safo.icos.modules.talent.offboarding.infrastructure.repository.adapter;

import com.safo.icos.modules.talent.offboarding.domain.model.OffboardingTemplate;
import com.safo.icos.modules.talent.offboarding.domain.repository.OffboardingTemplateRepository;
import com.safo.icos.modules.talent.offboarding.infrastructure.mapper.OffboardingMapper;
import com.safo.icos.modules.talent.offboarding.infrastructure.repository.jpa.OffboardingTemplateJpaRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class OffboardingTemplateRepositoryAdapter implements OffboardingTemplateRepository {
  private final OffboardingTemplateJpaRepository jpaRepository;
  private final OffboardingMapper mapper;

  @Override
  public OffboardingTemplate save(OffboardingTemplate template) {
    return mapper.toDomain(jpaRepository.save(mapper.toEntity(template)));
  }

  @Override
  public Optional<OffboardingTemplate> findById(UUID id) {
    return jpaRepository.findById(id).map(mapper::toDomain);
  }

  @Override
  public List<OffboardingTemplate> findByCompanyId(UUID companyId) {
    return jpaRepository.findByCompanyId(companyId).stream().map(mapper::toDomain).toList();
  }
}
