package com.safo.icos.modules.org.application;

import com.safo.icos.modules.org.application.dto.CompanyDto;
import com.safo.icos.modules.org.domain.model.Company;
import com.safo.icos.modules.org.domain.repository.CompanyRepository;
import com.safo.icos.security.permission.RequirePermission;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Application service for company management.
 */
@Service
@RequiredArgsConstructor
@Transactional
public class CompanyService {
  private final CompanyRepository companyRepository;

  /**
   * Creates a new company. Requires {@code company:manage} permission.
   *
   * @param name  company name
   * @param taxId company tax identifier
   * @return the created company
   */
  @RequirePermission("company:manage")
  public CompanyDto createCompany(String name, String taxId) {
    return createCompanyInternal(name, taxId);
  }

  /**
   * Creates a company without permission check.
   * Used internally during user registration flow.
   *
   * @param name  company name
   * @param taxId company tax identifier (optional)
   * @return the created company
   */
  @Transactional
  public CompanyDto createCompanyInternal(String name, String taxId) {
    Company company = Company.create(name, taxId);
    Company saved = companyRepository.save(company);
    return toDto(saved);
  }

  /**
   * Retrieves a company by its unique identifier.
   *
   * @param id company UUID
   * @return the company
   * @throws RuntimeException if not found
   */
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