package com.safo.icos.modules.org.domain.repository;

import com.safo.icos.modules.org.domain.model.Department;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository port for {@link Department} persistence.
 */
public interface DepartmentRepository {
  Optional<Department> findById(UUID id);
  List<Department> findByCompanyId(UUID companyId);
  boolean existsByCompanyIdAndName(UUID companyId, String name);
  Department save(Department department);
  void deleteById(UUID id);
}
