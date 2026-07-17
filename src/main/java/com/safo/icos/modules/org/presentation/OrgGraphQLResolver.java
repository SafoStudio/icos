package com.safo.icos.modules.org.presentation;

import com.safo.icos.modules.org.application.CompanyService;
import com.safo.icos.modules.org.application.DepartmentService;
import com.safo.icos.modules.org.application.PermissionService;
import com.safo.icos.modules.org.application.PositionService;
import com.safo.icos.modules.org.application.dto.CompanyDto;
import com.safo.icos.modules.org.application.dto.DepartmentDto;
import com.safo.icos.modules.org.application.dto.DepartmentMemberDto;
import com.safo.icos.modules.org.application.dto.PermissionDto;
import com.safo.icos.modules.org.application.dto.PositionDto;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class OrgGraphQLResolver {
  private final CompanyService companyService;
  private final DepartmentService departmentService;
  private final PositionService positionService;
  private final PermissionService permissionService;

  @QueryMapping
  public CompanyDto getCompany(@Argument String id) {
    return companyService.getCompany(UUID.fromString(id));
  }

  @QueryMapping(name = "departments")
  public List<DepartmentDto> getDepartmentsByCompany(@Argument String companyId) {
    return departmentService.getDepartmentsByCompany(UUID.fromString(companyId));
  }

  @QueryMapping(name = "positions")
  public List<PositionDto> getPositionsByCompany(@Argument String companyId) {
    return positionService.getPositionsByCompany(UUID.fromString(companyId));
  }

  @MutationMapping
  public CompanyDto createCompany(@Argument String name, @Argument String taxId) {
    return companyService.createCompany(name, taxId);
  }

  @MutationMapping
  public DepartmentDto createDepartment(@Argument String name, @Argument String description) {
    return departmentService.createDepartment(name, description);
  }

  @MutationMapping
  public DepartmentDto updateDepartment(@Argument String id, @Argument String name, @Argument String description) {
    return departmentService.updateDepartment(UUID.fromString(id), name, description);
  }

  @MutationMapping
  public PositionDto createPosition(@Argument String name, @Argument String description) {
    return positionService.createPosition(name, description);
  }

  @MutationMapping
  public PositionDto updatePosition(@Argument String id, @Argument String name, @Argument String description) {
    return positionService.updatePosition(UUID.fromString(id), name, description);
  }

  @MutationMapping
  public boolean deleteDepartment(@Argument String id) {
    return departmentService.deleteDepartment(UUID.fromString(id));
  }

  @MutationMapping
  public boolean deletePosition(@Argument String id) {
    return positionService.deletePosition(UUID.fromString(id));
  }

  @QueryMapping
  public List<PermissionDto> departmentPermissions(@Argument String departmentId, @Argument String roleName) {
    return departmentService.getDepartmentPermissions(UUID.fromString(departmentId), roleName);
  }

  @MutationMapping
  public boolean assignDepartmentPermission(@Argument String departmentId, @Argument String roleName, @Argument String permissionId) {
    departmentService.assignPermission(UUID.fromString(departmentId), roleName, UUID.fromString(permissionId));
    return true;
  }

  @MutationMapping
  public boolean revokeDepartmentPermission(@Argument String departmentId, @Argument String roleName, @Argument String permissionId) {
    departmentService.revokePermission(UUID.fromString(departmentId), roleName, UUID.fromString(permissionId));
    return true;
  }

  @QueryMapping
  public List<PermissionDto> permissions() {
    return permissionService.getAllPermissions();
  }

  @QueryMapping
  public DepartmentDto department(@Argument String id) {
    return departmentService.getDepartment(UUID.fromString(id));
  }

  @QueryMapping
  public List<DepartmentMemberDto> departmentMembers(@Argument String departmentId) {
    return departmentService.getDepartmentMembers(UUID.fromString(departmentId));
  }
}
