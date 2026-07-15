package com.safo.icos.modules.org.domain.repository;

import com.safo.icos.modules.org.domain.model.DepartmentPermission;
import java.util.List;
import java.util.UUID;

public interface DepartmentPermissionRepository {
    DepartmentPermission save(DepartmentPermission permission);
    List<DepartmentPermission> findByDepartmentIdAndRoleName(UUID departmentId, String roleName);
    void deleteByDepartmentIdAndRoleNameAndPermissionId(UUID departmentId, String roleName, UUID permissionId);
}
