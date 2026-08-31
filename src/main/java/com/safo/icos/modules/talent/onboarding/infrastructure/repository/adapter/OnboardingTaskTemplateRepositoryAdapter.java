package com.safo.icos.modules.talent.onboarding.infrastructure.repository.adapter;

import com.safo.icos.modules.talent.onboarding.domain.model.OnboardingTaskTemplate;
import com.safo.icos.modules.talent.onboarding.domain.repository.OnboardingTaskTemplateRepository;
import com.safo.icos.modules.talent.onboarding.infrastructure.mapper.OnboardingMapper;
import com.safo.icos.modules.talent.onboarding.infrastructure.repository.jpa.OnboardingTaskTemplateJpaRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class OnboardingTaskTemplateRepositoryAdapter implements OnboardingTaskTemplateRepository {
    private final OnboardingTaskTemplateJpaRepository jpaRepository;
    private final OnboardingMapper mapper;

    @Override
    public OnboardingTaskTemplate save(OnboardingTaskTemplate taskTemplate) {
        return mapper.toDomain(jpaRepository.save(mapper.toEntity(taskTemplate)));
    }

    @Override
    public Optional<OnboardingTaskTemplate> findById(UUID id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<OnboardingTaskTemplate> findByTemplateId(UUID templateId) {
        return jpaRepository.findByTemplateId(templateId).stream().map(mapper::toDomain).toList();
    }
}
