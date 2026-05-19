package com.safo.icos.modules.org.infrastructure.repository;

import com.safo.icos.modules.org.domain.model.Company;
import com.safo.icos.modules.org.domain.repository.CompanyRepository;
import com.safo.icos.modules.org.infrastructure.mapper.OrgMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class CompanyRepositoryImpl implements CompanyRepository {
    private final CompanyJpaRepository jpaRepository;
    private final OrgMapper mapper;

    @Override
    public Optional<Company> findById(UUID id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public Company save(Company company) {
        CompanyJpaEntity entity = mapper.toJpa(company);
        CompanyJpaEntity saved = jpaRepository.save(entity);
        return mapper.toDomain(saved);
    }
}
