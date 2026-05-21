package com.safo.icos.modules.org.presentation;

import com.safo.icos.modules.org.application.CompanyService;
import com.safo.icos.modules.org.application.DepartmentService;
import com.safo.icos.modules.org.application.PositionService;
import com.safo.icos.modules.org.application.dto.CompanyDto;
import com.safo.icos.modules.org.application.dto.DepartmentDto;
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
}
