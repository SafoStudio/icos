package com.safo.icos.modules.org.application;

import com.safo.icos.common.exception.BusinessException;
import com.safo.icos.modules.org.api.constant.OrgErrorCode;
import com.safo.icos.modules.org.application.dto.DepartmentDto;
import com.safo.icos.modules.org.domain.model.Department;
import com.safo.icos.modules.org.domain.repository.DepartmentRepository;
import com.safo.icos.security.context.TenantContext;
import com.safo.icos.security.permission.RequirePermission;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
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

  private DepartmentDto toDto(Department domain) {
    return DepartmentDto.builder()
            .id(domain.getId())
            .companyId(domain.getCompanyId())
            .name(domain.getName())
            .description(domain.getDescription())
            .build();
  }
}
