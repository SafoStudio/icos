package com.safo.icos.modules.org.infrastructure.repository.jpa;

import com.safo.icos.modules.org.infrastructure.repository.entity.DepartmentPermissionJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface DepartmentPermissionJpaRepository extends JpaRepository<DepartmentPermissionJpaEntity, UUID> {
  List<DepartmentPermissionJpaEntity> findByDepartmentIdAndRoleName(UUID departmentId, String roleName);
  void deleteByDepartmentIdAndRoleNameAndPermissionId(UUID departmentId, String roleName, UUID permissionId);
}
