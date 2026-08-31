package com.safo.icos.modules.org.infrastructure.repository.adapter;

import com.safo.icos.modules.org.domain.model.DepartmentPermission;
import com.safo.icos.modules.org.domain.repository.DepartmentPermissionRepository;
import com.safo.icos.modules.org.infrastructure.mapper.OrgMapper;
import com.safo.icos.modules.org.infrastructure.repository.entity.DepartmentPermissionJpaEntity;
import com.safo.icos.modules.org.infrastructure.repository.jpa.DepartmentPermissionJpaRepository;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class DepartmentPermissionRepositoryAdapter implements DepartmentPermissionRepository {

  private final DepartmentPermissionJpaRepository jpaRepository;
  private final OrgMapper mapper;

  @Override
  public DepartmentPermission save(DepartmentPermission permission) {
    DepartmentPermissionJpaEntity entity = mapper.toJpa(permission);
    DepartmentPermissionJpaEntity saved = jpaRepository.save(entity);
    return mapper.toDomain(saved);
  }

  @Override
  public List<DepartmentPermission> findByDepartmentIdAndRoleName(UUID departmentId, String roleName) {
    return jpaRepository.findByDepartmentIdAndRoleName(departmentId, roleName).stream()
            .map(mapper::toDomain)
            .toList();
  }

  @Override
  public void deleteByDepartmentIdAndRoleNameAndPermissionId(UUID departmentId, String roleName, UUID permissionId) {
    jpaRepository.deleteByDepartmentIdAndRoleNameAndPermissionId(departmentId, roleName, permissionId);
  }
}
