package com.safo.icos.modules.talent.recruitment.application;

import com.safo.icos.common.exception.BusinessException;
import com.safo.icos.modules.identity.auth.api.constant.AuthErrorCode;
import com.safo.icos.modules.talent.recruitment.api.constant.RecruitmentErrorCode;
import com.safo.icos.modules.talent.recruitment.application.dto.CreateVacancyRequest;
import com.safo.icos.modules.talent.recruitment.application.dto.VacancyDto;
import com.safo.icos.modules.talent.recruitment.domain.model.Vacancy;
import com.safo.icos.modules.talent.recruitment.domain.model.VacancyStage;
import com.safo.icos.modules.talent.recruitment.domain.model.enums.PipelineStageType;
import com.safo.icos.modules.talent.recruitment.domain.repository.VacancyRepository;
import com.safo.icos.modules.talent.recruitment.infrastructure.mapper.RecruitmentMapper;
import com.safo.icos.modules.talent.recruitment.infrastructure.repository.jpa.VacancyStageJpaRepository;
import com.safo.icos.security.context.IcosUserPrincipal;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Application service for vacancy management.
 * Creates vacancies with a default recruitment pipeline
 * and provides lookup by company.
 */
@Service
@RequiredArgsConstructor
@Transactional
public class VacancyService {

  private final VacancyRepository vacancyRepository;
  private final VacancyStageJpaRepository vacancyStageJpaRepository;
  private final RecruitmentMapper mapper;

  /**
   * Creates a new vacancy with a default 5-stage pipeline:
   * Applied → Screening → Technical Interview → Final Interview → Offer.
   *
   * @param request the vacancy creation payload
   * @return the created vacancy
   */
  public VacancyDto createVacancy(CreateVacancyRequest request) {
    IcosUserPrincipal principal = getPrincipal();

    Vacancy vacancy = Vacancy.create(
            request.getCompanyId(),
            request.getDepartmentId(),
            request.getPositionId(),
            request.getTitle(),
            request.getDescription(),
            request.getRequirements(),
            principal.getUserId()
    );

    Vacancy savedVacancy = vacancyRepository.save(vacancy);
    createDefaultStages(savedVacancy.getId());

    return toDto(savedVacancy);
  }

  /**
   * Returns all vacancies for a given company.
   *
   * @param companyId the company UUID
   * @return list of vacancies
   */
  public List<VacancyDto> getVacanciesByCompany(UUID companyId) {
    return vacancyRepository.findByCompanyId(companyId).stream()
            .map(this::toDto)
            .toList();
  }

  /**
   * Retrieves a single vacancy by ID.
   *
   * @param vacancyId the vacancy UUID
   * @return the vacancy DTO
   * @throws BusinessException if not found
   */
  public VacancyDto getVacancy(UUID vacancyId) {
    return vacancyRepository.findById(vacancyId)
            .map(this::toDto)
            .orElseThrow(() -> new BusinessException(
                    "Vacancy not found", RecruitmentErrorCode.VACANCY_NOT_FOUND));
  }

  private IcosUserPrincipal getPrincipal() {
    var authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication == null
            || !(authentication.getPrincipal() instanceof IcosUserPrincipal principal)) {
      throw new BusinessException("User not authenticated", AuthErrorCode.TOKEN_NOT_FOUND);
    }
    return principal;
  }

  private void createDefaultStages(UUID vacancyId) {
    List<VacancyStage> defaultStages = List.of(
            VacancyStage.create(vacancyId, "Applied", PipelineStageType.APPLIED, 0),
            VacancyStage.create(vacancyId, "Screening", PipelineStageType.SCREENING, 1),
            VacancyStage.create(vacancyId, "Technical Interview", PipelineStageType.TECHNICAL, 2),
            VacancyStage.create(vacancyId, "Final Interview", PipelineStageType.HR, 3),
            VacancyStage.create(vacancyId, "Offer", PipelineStageType.OFFER, 4)
    );
    defaultStages.forEach(stage -> vacancyStageJpaRepository.save(mapper.toEntity(stage)));
  }

  private VacancyDto toDto(Vacancy vacancy) {
    return VacancyDto.builder()
            .id(vacancy.getId())
            .companyId(vacancy.getCompanyId())
            .departmentId(vacancy.getDepartmentId())
            .positionId(vacancy.getPositionId())
            .title(vacancy.getTitle())
            .description(vacancy.getDescription())
            .requirements(vacancy.getRequirements())
            .status(vacancy.getStatus())
            .createdAt(vacancy.getCreatedAt())
            .build();
  }
}
