package com.safo.icos.modules.talent.offboarding.infrastructure.repository.adapter;

import com.safo.icos.modules.talent.offboarding.domain.model.OffboardingProcess;
import com.safo.icos.modules.talent.offboarding.domain.repository.OffboardingProcessRepository;
import com.safo.icos.modules.talent.offboarding.infrastructure.mapper.OffboardingMapper;
import com.safo.icos.modules.talent.offboarding.infrastructure.repository.jpa.OffboardingProcessJpaRepository;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class OffboardingProcessRepositoryAdapter implements OffboardingProcessRepository {
  private final OffboardingProcessJpaRepository jpaRepository;
  private final OffboardingMapper mapper;

  @Override
  public OffboardingProcess save(OffboardingProcess process) {
    return mapper.toDomain(jpaRepository.save(mapper.toEntity(process)));
  }

  @Override
  public Optional<OffboardingProcess> findById(UUID id) {
    return jpaRepository.findById(id).map(mapper::toDomain);
  }

  @Override
  public Optional<OffboardingProcess> findByMembershipId(UUID membershipId) {
    return jpaRepository.findByMembershipId(membershipId).map(mapper::toDomain);
  }
}