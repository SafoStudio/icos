package com.safo.icos.modules.talent.recruitment.domain.repository;

import com.safo.icos.modules.talent.recruitment.domain.model.Application;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ApplicationRepository {
    Application save(Application application);
    Optional<Application> findById(UUID id);
    List<Application> findByVacancyId(UUID vacancyId);
    List<Application> findByCandidateId(UUID candidateId);
}
