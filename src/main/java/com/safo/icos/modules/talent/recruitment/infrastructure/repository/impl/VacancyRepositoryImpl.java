package com.safo.icos.modules.talent.recruitment.infrastructure.repository.impl;

import com.safo.icos.modules.talent.recruitment.domain.model.Vacancy;
import com.safo.icos.modules.talent.recruitment.domain.repository.VacancyRepository;
import com.safo.icos.modules.talent.recruitment.infrastructure.mapper.RecruitmentMapper;
import com.safo.icos.modules.talent.recruitment.infrastructure.repository.entity.VacancyJpaEntity;
import com.safo.icos.modules.talent.recruitment.infrastructure.repository.jpa.VacancyJpaRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class VacancyRepositoryImpl implements VacancyRepository {
    private final VacancyJpaRepository jpaRepository;
    private final RecruitmentMapper mapper;

    @Override
    public Vacancy save(Vacancy vacancy) {
        VacancyJpaEntity entity = mapper.toEntity(vacancy);
        VacancyJpaEntity saved = jpaRepository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<Vacancy> findById(UUID id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<Vacancy> findByCompanyId(UUID companyId) {
        return jpaRepository.findByCompanyId(companyId).stream()
                .map(mapper::toDomain)
                .toList();
    }
}
