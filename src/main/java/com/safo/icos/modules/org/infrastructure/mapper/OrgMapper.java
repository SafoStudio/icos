package com.safo.icos.modules.org.infrastructure.mapper;

import com.safo.icos.modules.org.domain.model.Company;
import com.safo.icos.modules.org.domain.model.Department;
import com.safo.icos.modules.org.domain.model.Position;
import com.safo.icos.modules.org.infrastructure.repository.CompanyJpaEntity;
import com.safo.icos.modules.org.infrastructure.repository.PositionJpaEntity;
import com.safo.icos.modules.user.infrastructure.repository.DepartmentJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class OrgMapper {

    public Company toDomain(CompanyJpaEntity entity) {
        return Company.builder()
                .id(entity.getId())
                .name(entity.getName())
                .taxId(entity.getTaxId())
                .policyVersion(entity.getPolicyVersion())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    public CompanyJpaEntity toJpa(Company domain) {
        return CompanyJpaEntity.builder()
                .id(domain.getId())
                .name(domain.getName())
                .taxId(domain.getTaxId())
                .policyVersion(domain.getPolicyVersion())
                .createdAt(domain.getCreatedAt())
                .updatedAt(domain.getUpdatedAt())
                .build();
    }

    public Department toDomain(DepartmentJpaEntity entity) {
        return Department.builder()
                .id(entity.getId())
                .companyId(entity.getCompanyId())
                .name(entity.getName())
                .managerId(entity.getManagerId())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    public DepartmentJpaEntity toJpa(Department domain) {
        return DepartmentJpaEntity.builder()
                .id(domain.getId())
                .companyId(domain.getCompanyId())
                .name(domain.getName())
                .managerId(domain.getManagerId())
                .createdAt(domain.getCreatedAt())
                .updatedAt(domain.getUpdatedAt())
                .build();
    }

    public Position toDomain(PositionJpaEntity entity) {
        return Position.builder()
                .id(entity.getId())
                .companyId(entity.getCompanyId())
                .name(entity.getName())
                .description(entity.getDescription())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    public PositionJpaEntity toJpa(Position domain) {
        return PositionJpaEntity.builder()
                .id(domain.getId())
                .companyId(domain.getCompanyId())
                .name(domain.getName())
                .description(domain.getDescription())
                .createdAt(domain.getCreatedAt())
                .updatedAt(domain.getUpdatedAt())
                .build();
    }
}
