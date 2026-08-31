package com.safo.icos.modules.talent.onboarding.infrastructure.repository.adapter;

import com.safo.icos.modules.talent.onboarding.domain.model.OnboardingTemplate;
import com.safo.icos.modules.talent.onboarding.domain.repository.OnboardingTemplateRepository;
import com.safo.icos.modules.talent.onboarding.infrastructure.mapper.OnboardingMapper;
import com.safo.icos.modules.talent.onboarding.infrastructure.repository.jpa.OnboardingTemplateJpaRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class OnboardingTemplateRepositoryAdapter implements OnboardingTemplateRepository {
    private final OnboardingTemplateJpaRepository jpaRepository;
    private final OnboardingMapper mapper;

    @Override
    public OnboardingTemplate save(OnboardingTemplate template) {
        return mapper.toDomain(jpaRepository.save(mapper.toEntity(template)));
    }

    @Override
    public Optional<OnboardingTemplate> findById(UUID id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<OnboardingTemplate> findByCompanyId(UUID companyId) {
        return jpaRepository.findByCompanyId(companyId).stream().map(mapper::toDomain).toList();
    }
}
