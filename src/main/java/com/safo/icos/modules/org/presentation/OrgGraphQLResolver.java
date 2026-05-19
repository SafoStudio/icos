package com.safo.icos.modules.org.presentation;

import com.safo.icos.modules.org.application.OrgService;
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
    private final OrgService orgService;

    @QueryMapping
    public CompanyDto company(@Argument String id) {
        return orgService.getCompany(UUID.fromString(id));
    }

    @QueryMapping
    public List<DepartmentDto> departments(@Argument String companyId) {
        return orgService.getDepartmentsByCompany(UUID.fromString(companyId));
    }

    @QueryMapping
    public List<PositionDto> positions(@Argument String companyId) {
        return orgService.getPositionsByCompany(UUID.fromString(companyId));
    }

    @MutationMapping
    public CompanyDto createCompany(@Argument String name, @Argument String taxId) {
        return orgService.createCompany(name, taxId);
    }

    @MutationMapping
    public DepartmentDto createDepartment(@Argument String companyId, @Argument String name, @Argument String managerId) {
        return orgService.createDepartment(
                UUID.fromString(companyId),
                name,
                managerId != null ? UUID.fromString(managerId) : null
        );
    }

    @MutationMapping
    public PositionDto createPosition(@Argument String companyId, @Argument String name, @Argument String description) {
        return orgService.createPosition(UUID.fromString(companyId), name, description);
    }
}
