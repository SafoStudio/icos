package com.safo.icos.modules.org.infrastructure.repository;

import com.safo.icos.modules.org.domain.model.Department;
import com.safo.icos.modules.org.domain.repository.DepartmentRepository;
import com.safo.icos.modules.org.infrastructure.mapper.OrgMapper;
import com.safo.icos.modules.user.infrastructure.repository.DepartmentJpaEntity;
import com.safo.icos.modules.user.infrastructure.repository.DepartmentJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

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
        // Since we don't have a direct method in DepartmentJpaRepository yet, we filter for now
        // or we could add it to DepartmentJpaRepository.
        return jpaRepository.findAll().stream()
                .filter(d -> d.getCompanyId().equals(companyId))
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Department save(Department department) {
        DepartmentJpaEntity entity = mapper.toJpa(department);
        DepartmentJpaEntity saved = jpaRepository.save(entity);
        return mapper.toDomain(saved);
    }
}
