package com.safo.icos.modules.org.infrastructure.repository.jpa;

import com.safo.icos.modules.org.infrastructure.repository.entity.DepartmentJpaEntity;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DepartmentJpaRepository extends JpaRepository<DepartmentJpaEntity, UUID> {
  boolean existsByCompanyIdAndName(UUID companyId, String name);

  List<DepartmentJpaEntity> findByCompanyId(UUID companyId);
}
