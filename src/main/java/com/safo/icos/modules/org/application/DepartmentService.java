package com.safo.icos.modules.org.application;

import com.safo.icos.modules.org.application.dto.DepartmentDto;
import com.safo.icos.modules.org.domain.model.Department;
import com.safo.icos.modules.org.domain.repository.DepartmentRepository;
import com.safo.icos.common.constant.ErrorCode;
import com.safo.icos.common.exception.BusinessException;
import com.safo.icos.security.context.TenantContext;
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
public class DepartmentService {
    private final DepartmentRepository departmentRepository;

    @RequirePermission("department:manage")
    public DepartmentDto createDepartment(String name, String description, UUID managerId) {
        UUID companyId = UUID.fromString(TenantContext.getTenantId());
        
        if (departmentRepository.existsByCompanyIdAndName(companyId, name)) {
            throw new BusinessException("Department already exists", ErrorCode.DEPT_ALREADY_EXISTS);
        }
        
        Department department = Department.create(companyId, name, description, managerId);
        Department saved = departmentRepository.save(department);
        return toDto(saved);
    }

    public List<DepartmentDto> getDepartmentsByCompany(UUID companyId) {
        return departmentRepository.findByCompanyId(companyId).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    private DepartmentDto toDto(Department domain) {
        return DepartmentDto.builder()
                .id(domain.getId())
                .companyId(domain.getCompanyId())
                .name(domain.getName())
                .description(domain.getDescription())
                .managerId(domain.getManagerId())
                .build();
    }
}