package com.safo.icos.modules.talent.recruitment.application;

import com.safo.icos.common.exception.BusinessException;
import com.safo.icos.modules.identity.auth.api.constant.AuthErrorCode;
import com.safo.icos.modules.talent.recruitment.api.constant.RecruitmentErrorCode;
import com.safo.icos.modules.talent.recruitment.application.dto.CreateVacancyRequest;
import com.safo.icos.modules.talent.recruitment.application.dto.VacancyDto;
import com.safo.icos.modules.talent.recruitment.domain.model.Vacancy;
import com.safo.icos.modules.talent.recruitment.domain.model.VacancyStage;
import com.safo.icos.modules.talent.recruitment.domain.model.enums.PipelineStageType;
import com.safo.icos.modules.talent.recruitment.domain.model.enums.VacancyStatus;
import com.safo.icos.modules.talent.recruitment.domain.repository.VacancyRepository;
import com.safo.icos.modules.talent.recruitment.infrastructure.mapper.RecruitmentMapper;
import com.safo.icos.modules.talent.recruitment.infrastructure.repository.jpa.VacancyStageJpaRepository;
import com.safo.icos.security.context.IcosUserPrincipal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
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

    VacancyStatus status = request.getStatus() != null
            ? request.getStatus()
            : VacancyStatus.DRAFT;

    Vacancy vacancy = Vacancy.builder()
            .id(UUID.randomUUID())
            .companyId(request.getCompanyId())
            .departmentId(request.getDepartmentId())
            .positionId(request.getPositionId())
            .title(request.getTitle())
            .description(request.getDescription())
            .requirements(request.getRequirements())
            .status(status)
            .createdBy(principal.getUserId())
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build();

    Vacancy savedVacancy = vacancyRepository.save(vacancy);
    createDefaultStages(savedVacancy.getId());

    return mapper.toDto(savedVacancy);
  }

  /**
   * Returns all vacancies for a given company.
   *
   * @param companyId the company UUID
   * @return list of vacancies
   */
  public List<VacancyDto> getVacanciesByCompany(UUID companyId) {
    return vacancyRepository.findByCompanyId(companyId).stream()
            .map(mapper::toDto)
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
            .map(mapper::toDto)
            .orElseThrow(() -> new BusinessException(
                    "Vacancy not found", RecruitmentErrorCode.VACANCY_NOT_FOUND));
  }

  /**
   * Updates the status of an existing vacancy.
   *
   * @param vacancyId the vacancy UUID
   * @param status the new status (DRAFT, OPEN, CLOSED, PAUSED)
   * @return the updated vacancy DTO
   * @throws BusinessException if vacancy not found
   */
  public VacancyDto updateVacancyStatus(UUID vacancyId, VacancyStatus status) {
    Vacancy vacancy = vacancyRepository.findById(vacancyId)
            .orElseThrow(() -> new BusinessException(
                    "Vacancy not found", RecruitmentErrorCode.VACANCY_NOT_FOUND));

    vacancy.setStatus(status);
    Vacancy savedVacancy = vacancyRepository.save(vacancy);

    log.info("Updated vacancy {} status to {}", vacancyId, status);
    return mapper.toDto(savedVacancy);
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
}