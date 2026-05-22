package com.safo.icos.modules.recruitment.infrastructure.repository.impl;

import com.safo.icos.modules.recruitment.domain.model.Application;
import com.safo.icos.modules.recruitment.domain.repository.ApplicationRepository;
import com.safo.icos.modules.recruitment.infrastructure.mapper.RecruitmentMapper;
import com.safo.icos.modules.recruitment.infrastructure.repository.entity.ApplicationJpaEntity;
import com.safo.icos.modules.recruitment.infrastructure.repository.jpa.ApplicationJpaRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ApplicationRepositoryImpl implements ApplicationRepository {
    private final ApplicationJpaRepository jpaRepository;
    private final RecruitmentMapper mapper;

    @Override
    public Application save(Application application) {
        ApplicationJpaEntity entity = mapper.toEntity(application);
        ApplicationJpaEntity saved = jpaRepository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<Application> findById(UUID id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<Application> findByVacancyId(UUID vacancyId) {
        return jpaRepository.findByVacancyId(vacancyId).stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public List<Application> findByCandidateId(UUID candidateId) {
        return jpaRepository.findByCandidateId(candidateId).stream()
                .map(mapper::toDomain)
                .toList();
    }
}
