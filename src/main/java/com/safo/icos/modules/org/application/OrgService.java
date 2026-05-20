package com.safo.icos.modules.org.application;

import com.safo.icos.modules.org.application.dto.*;
import com.safo.icos.modules.org.domain.model.*;
import com.safo.icos.modules.org.domain.repository.*;
import com.safo.icos.security.permission.RequirePermission;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class OrgService {
    private final CompanyRepository companyRepository;
    private final DepartmentRepository departmentRepository;
    private final PositionRepository positionRepository;

    // --- Company ---
    @RequirePermission("org:manage")
    public CompanyDto createCompany(String name, String taxId) {
        return createCompanyInternal(name, taxId);
    }

    @Transactional
    public CompanyDto createCompanyInternal(String name, String taxId) {
        Company company = Company.create(name, taxId);
        Company saved = companyRepository.save(company);
        return toDto(saved);
    }

    public CompanyDto getCompany(UUID id) {
        return companyRepository.findById(id)
                .map(this::toDto)
                .orElseThrow(() -> new RuntimeException("Company not found"));
    }

    // --- Department ---
    @RequirePermission("dept:manage")
    public DepartmentDto createDepartment(UUID companyId, String name, UUID managerId) {
        Department department = Department.create(companyId, name, managerId);
        Department saved = departmentRepository.save(department);
        return toDto(saved);
    }

    public List<DepartmentDto> getDepartmentsByCompany(UUID companyId) {
        return departmentRepository.findByCompanyId(companyId).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    // --- Position ---
    @RequirePermission("pos:manage")
    public PositionDto createPosition(UUID companyId, String name, String description) {
        Position position = Position.create(companyId, name, description);
        Position saved = positionRepository.save(position);
        return toDto(saved);
    }

    public List<PositionDto> getPositionsByCompany(UUID companyId) {
        return positionRepository.findByCompanyId(companyId).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    // --- Mappers to DTO ---
    private CompanyDto toDto(Company domain) {
        return CompanyDto.builder()
                .id(domain.getId())
                .name(domain.getName())
                .taxId(domain.getTaxId())
                .build();
    }

    private DepartmentDto toDto(Department domain) {
        return DepartmentDto.builder()
                .id(domain.getId())
                .companyId(domain.getCompanyId())
                .name(domain.getName())
                .managerId(domain.getManagerId())
                .build();
    }

    private PositionDto toDto(Position domain) {
        return PositionDto.builder()
                .id(domain.getId())
                .companyId(domain.getCompanyId())
                .name(domain.getName())
                .description(domain.getDescription())
                .build();
    }
}
