package com.safo.icos.modules.talent.recruitment.domain.repository;

import com.safo.icos.modules.talent.recruitment.domain.model.Vacancy;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface VacancyRepository {
    Vacancy save(Vacancy vacancy);
    Optional<Vacancy> findById(UUID id);
    List<Vacancy> findByCompanyId(UUID companyId);
}
