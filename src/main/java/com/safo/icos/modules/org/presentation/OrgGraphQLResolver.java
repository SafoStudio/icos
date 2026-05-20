package com.safo.icos.modules.org.presentation;

import com.safo.icos.modules.org.application.CompanyService;
import com.safo.icos.modules.org.application.DepartmentService;
import com.safo.icos.modules.org.application.PositionService;
import com.safo.icos.modules.org.application.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.List;
import java.util.UUID;

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

    @QueryMapping
    public List<DepartmentDto> getDepartmentsByCompany(@Argument String companyId) {
        return departmentService.getDepartmentsByCompany(UUID.fromString(companyId));
    }

    @QueryMapping
    public List<PositionDto> getPositionsByCompany(@Argument String companyId) {
        return positionService.getPositionsByCompany(UUID.fromString(companyId));
    }

    @MutationMapping
    public CompanyDto createCompany(@Argument String name, @Argument String taxId) {
        return companyService.createCompany(name, taxId);
    }

    @MutationMapping
    public DepartmentDto createDepartment(@Argument String companyId, @Argument String name, @Argument String managerId) {
        return departmentService.createDepartment(
                UUID.fromString(companyId),
                name,
                managerId != null ? UUID.fromString(managerId) : null
        );
    }

    @MutationMapping
    public PositionDto createPosition(@Argument String companyId, @Argument String name, @Argument String description) {
        return positionService.createPosition(UUID.fromString(companyId), name, description);
    }
}