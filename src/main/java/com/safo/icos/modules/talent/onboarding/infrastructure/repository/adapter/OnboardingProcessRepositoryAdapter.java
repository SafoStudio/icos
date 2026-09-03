package com.safo.icos.modules.talent.onboarding.infrastructure.repository.adapter;

import com.safo.icos.modules.talent.onboarding.domain.model.OnboardingProcess;
import com.safo.icos.modules.talent.onboarding.domain.repository.OnboardingProcessRepository;
import com.safo.icos.modules.talent.onboarding.infrastructure.mapper.OnboardingMapper;
import com.safo.icos.modules.talent.onboarding.infrastructure.repository.jpa.OnboardingProcessJpaRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class OnboardingProcessRepositoryAdapter implements OnboardingProcessRepository {
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

    @Override
    public List<OnboardingProcess> findByCompanyId(UUID companyId) {
        return jpaRepository.findByCompanyId(companyId).stream().map(mapper::toDomain).toList();
    }
}
