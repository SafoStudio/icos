package com.safo.icos.modules.org.application;

import com.safo.icos.modules.org.application.dto.CompanyDto;
import com.safo.icos.modules.org.domain.model.Company;
import com.safo.icos.modules.org.domain.repository.CompanyRepository;
import com.safo.icos.security.permission.RequirePermission;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class CompanyService {
    private final CompanyRepository companyRepository;

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

    private CompanyDto toDto(Company domain) {
        return CompanyDto.builder()
                .id(domain.getId())
                .name(domain.getName())
                .taxId(domain.getTaxId())
                .build();
    }
}