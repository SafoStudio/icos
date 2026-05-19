package com.safo.icos.modules.org.domain.repository;

import com.safo.icos.modules.org.domain.model.Company;
import java.util.Optional;
import java.util.UUID;

public interface CompanyRepository {
    Optional<Company> findById(UUID id);
    Company save(Company company);
}
