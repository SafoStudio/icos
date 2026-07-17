package com.safo.icos.modules.org.application;

import com.safo.icos.common.exception.BusinessException;
import com.safo.icos.modules.identity.user.infrastructure.repository.entity.UserJpaEntity;
import com.safo.icos.modules.identity.user.infrastructure.repository.jpa.MembershipJpaRepository;
import com.safo.icos.modules.identity.user.infrastructure.repository.jpa.UserJpaRepository;
import com.safo.icos.modules.org.infrastructure.repository.entity.PositionJpaEntity;
import com.safo.icos.modules.org.infrastructure.repository.jpa.PositionJpaRepository;
import com.safo.icos.modules.org.api.constant.OrgErrorCode;
import com.safo.icos.modules.org.application.dto.DepartmentDto;
import com.safo.icos.modules.org.application.dto.DepartmentMemberDto;
import com.safo.icos.modules.org.application.dto.PermissionDto;
import com.safo.icos.modules.org.domain.model.Department;
import com.safo.icos.modules.org.domain.model.DepartmentPermission;
import com.safo.icos.modules.org.domain.repository.DepartmentPermissionRepository;
import com.safo.icos.modules.org.domain.repository.DepartmentRepository;
import com.safo.icos.modules.identity.user.infrastructure.repository.entity.PermissionJpaEntity;
import com.safo.icos.modules.identity.user.infrastructure.repository.jpa.PermissionJpaRepository;
import com.safo.icos.security.context.TenantContext;
import com.safo.icos.security.permission.RequirePermission;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Application service for department management.
 */
@Service
@RequiredArgsConstructor
@Transactional
public class DepartmentService {
  private final DepartmentRepository departmentRepository;
  private final DepartmentPermissionRepository departmentPermissionRepository;
  private final PermissionJpaRepository permissionJpaRepository;
  private final MembershipJpaRepository membershipJpaRepository;
  private final UserJpaRepository userJpaRepository;
  private final PositionJpaRepository positionJpaRepository;

  /**
   * Updates an existing department. Requires {@code department:manage} permission.
   *
   * @param id          department UUID
   * @param name        new name
   * @param description new description
   * @return the updated department
   * @throws BusinessException if the department is not found
   */
  @RequirePermission("department:manage")
  public DepartmentDto updateDepartment(UUID id, String name, String description) {
    Department department = departmentRepository.findById(id)
            .orElseThrow(() -> new BusinessException("Department not found", OrgErrorCode.DEPT_NOT_FOUND));
    department.update(name, description);
    Department saved = departmentRepository.save(department);
    return toDto(saved);
  }

  /**
   * Creates a new department in the current tenant's company.
   * Requires {@code department:manage} permission.
   *
   * @param name        department name
   * @param description department description
   * @return the created department
   * @throws BusinessException if a department with this name already exists
   */
  @RequirePermission("department:manage")
  public DepartmentDto createDepartment(String name, String description) {
    UUID companyId = UUID.fromString(TenantContext.getTenantId());
    if (departmentRepository.existsByCompanyIdAndName(companyId, name)) {
      throw new BusinessException("Department already exists", OrgErrorCode.DEPT_ALREADY_EXISTS);
    }
    Department department = Department.create(companyId, name, description);
    Department saved = departmentRepository.save(department);
    return toDto(saved);
  }

  /**
   * Returns all departments in the given company.
   *
   * @param companyId company UUID
   * @return list of departments, never {@code null}
   */
  public List<DepartmentDto> getDepartmentsByCompany(UUID companyId) {
    List<Department> departments = departmentRepository.findByCompanyId(companyId);
    if (departments == null) {
      return Collections.emptyList();
    }
    return departments.stream()
            .map(this::toDto)
            .toList();
  }

  /**
   * Deletes an existing department. Requires {@code department:manage} permission.
   *
   * @param id department UUID
   * @return true if deleted, false if not found
   */
  @RequirePermission("department:manage")
  public boolean deleteDepartment(UUID id) {
    if (departmentRepository.findById(id).isPresent()) {
      departmentRepository.deleteById(id);
      return true;
    }
    return false;
  }

  @RequirePermission("department:manage")
  public void assignPermission(UUID departmentId, String roleName, UUID permissionId) {
    if (!permissionJpaRepository.existsById(permissionId)) {
      throw new BusinessException("Permission not found", OrgErrorCode.PERMISSION_NOT_FOUND);
    }
    DepartmentPermission permission = DepartmentPermission.create(departmentId, roleName, permissionId);
    departmentPermissionRepository.save(permission);
  }

  @RequirePermission("department:manage")
  public void revokePermission(UUID departmentId, String roleName, UUID permissionId) {
    departmentPermissionRepository.deleteByDepartmentIdAndRoleNameAndPermissionId(departmentId, roleName, permissionId);
  }

  public List<PermissionDto> getDepartmentPermissions(UUID departmentId, String roleName) {
    return departmentPermissionRepository.findByDepartmentIdAndRoleName(departmentId, roleName)
            .stream()
            .map(dp -> {
              String permissionName = permissionJpaRepository.findById(dp.getPermissionId())
                      .map(PermissionJpaEntity::getName)
                      .orElse(null);
              return PermissionDto.builder()
                      .id(dp.getPermissionId())
                      .name(permissionName)
                      .build();
            })
            .toList();
  }

  /**
   * Returns a department by its ID.
   *
   * @param id department UUID
   * @return the department DTO
   * @throws BusinessException if the department is not found
   */
  public DepartmentDto getDepartment(UUID id) {
    Department department = departmentRepository.findById(id)
            .orElseThrow(() -> new BusinessException("Department not found", OrgErrorCode.DEPT_NOT_FOUND));
    return toDto(department);
  }

  private DepartmentDto toDto(Department domain) {
    return DepartmentDto.builder()
            .id(domain.getId())
            .companyId(domain.getCompanyId())
            .name(domain.getName())
            .description(domain.getDescription())
            .build();
  }

  public List<DepartmentMemberDto> getDepartmentMembers(UUID departmentId) {
    var memberships = membershipJpaRepository.findByDepartmentIdAndActiveTrue(departmentId);

    return memberships.stream()
            .map(membership -> {
              UserJpaEntity user = userJpaRepository.findById(membership.getUserId())
                      .orElseThrow();

              String positionName = null;
              if (membership.getPositionId() != null) {
                positionName = positionJpaRepository.findById(membership.getPositionId())
                        .map(PositionJpaEntity::getName)
                        .orElse(null);
              }

              return DepartmentMemberDto.builder()
                      .userId(user.getId())
                      .firstName(user.getFirstName())
                      .lastName(user.getLastName())
                      .positionName(positionName)
                      .build();
            })
            .collect(Collectors.toList());
  }
}
