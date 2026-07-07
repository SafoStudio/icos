package com.safo.icos.modules.org.infrastructure.repository;

import com.safo.icos.modules.org.domain.model.Department;
import com.safo.icos.modules.org.domain.repository.DepartmentRepository;
import com.safo.icos.modules.org.infrastructure.mapper.OrgMapper;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class DepartmentRepositoryImpl implements DepartmentRepository {
  private final DepartmentJpaRepository jpaRepository;
  private final OrgMapper mapper;

  @Override
  public Optional<Department> findById(UUID id) {
    return jpaRepository.findById(id).map(mapper::toDomain);
  }

  @Override
  public List<Department> findByCompanyId(UUID companyId) {
    return jpaRepository.findByCompanyId(companyId).stream()
            .map(mapper::toDomain)
            .collect(Collectors.toList());
  }

  @Override
  public boolean existsByCompanyIdAndName(UUID companyId, String name) {
    return jpaRepository.existsByCompanyIdAndName(companyId, name);
  }

  @Override
  public Department save(Department department) {
    DepartmentJpaEntity entity = mapper.toJpa(department);
    DepartmentJpaEntity saved = jpaRepository.save(entity);
    return mapper.toDomain(saved);
  }
}
