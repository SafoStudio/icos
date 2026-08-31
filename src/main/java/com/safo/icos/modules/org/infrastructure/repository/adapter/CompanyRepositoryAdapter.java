package com.safo.icos.modules.org.infrastructure.repository.adapter;

import com.safo.icos.modules.org.domain.model.Company;
import com.safo.icos.modules.org.domain.repository.CompanyRepository;
import com.safo.icos.modules.org.infrastructure.mapper.OrgMapper;
import com.safo.icos.modules.org.infrastructure.repository.entity.CompanyJpaEntity;
import com.safo.icos.modules.org.infrastructure.repository.jpa.CompanyJpaRepository;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class CompanyRepositoryAdapter implements CompanyRepository {
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
