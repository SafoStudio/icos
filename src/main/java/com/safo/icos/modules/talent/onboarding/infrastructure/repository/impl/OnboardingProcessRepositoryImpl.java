package com.safo.icos.modules.talent.onboarding.infrastructure.repository.impl;

import com.safo.icos.modules.talent.onboarding.domain.model.OnboardingProcess;
import com.safo.icos.modules.talent.onboarding.domain.repository.OnboardingProcessRepository;
import com.safo.icos.modules.talent.onboarding.infrastructure.mapper.OnboardingMapper;
import com.safo.icos.modules.talent.onboarding.infrastructure.repository.jpa.OnboardingProcessJpaRepository;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class OnboardingProcessRepositoryImpl implements OnboardingProcessRepository {
    private final OnboardingProcessJpaRepository jpaRepository;
    private final OnboardingMapper mapper;

    @Override
    public OnboardingProcess save(OnboardingProcess process) {
        return mapper.toDomain(jpaRepository.save(mapper.toEntity(process)));
    }

    @Override
    public Optional<OnboardingProcess> findById(UUID id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public Optional<OnboardingProcess> findByMembershipId(UUID membershipId) {
        return jpaRepository.findByMembershipId(membershipId).map(mapper::toDomain);
    }
}
