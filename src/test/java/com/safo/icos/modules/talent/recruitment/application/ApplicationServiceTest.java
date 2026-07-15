package com.safo.icos.modules.talent.recruitment.application;

import com.safo.icos.common.exception.BusinessException;
import com.safo.icos.modules.talent.recruitment.api.event.CandidateStageChangedEvent;
import com.safo.icos.modules.talent.recruitment.application.dto.ApplicationDto;
import com.safo.icos.modules.talent.recruitment.application.dto.KanbanBoardDto;
import com.safo.icos.modules.talent.recruitment.domain.model.Application;
import com.safo.icos.modules.talent.recruitment.domain.model.VacancyStage;
import com.safo.icos.modules.talent.recruitment.domain.model.enums.ApplicationStatus;
import com.safo.icos.modules.talent.recruitment.domain.model.enums.PipelineStageType;
import com.safo.icos.modules.talent.recruitment.domain.repository.ApplicationRepository;
import com.safo.icos.modules.talent.recruitment.domain.repository.VacancyRepository;
import com.safo.icos.modules.talent.recruitment.infrastructure.mapper.RecruitmentMapper;
import com.safo.icos.modules.talent.recruitment.infrastructure.repository.entity.VacancyStageJpaEntity;
import com.safo.icos.modules.talent.recruitment.infrastructure.repository.jpa.VacancyStageJpaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ApplicationServiceTest {

  @Mock private ApplicationRepository applicationRepository;
  @Mock private VacancyRepository vacancyRepository;
  @Mock private VacancyStageJpaRepository vacancyStageJpaRepository;
  @Mock private RecruitmentMapper mapper;
  @Mock private ApplicationEventPublisher eventPublisher;

  @InjectMocks
  private ApplicationService applicationService;

  private static final UUID CANDIDATE_ID = UUID.randomUUID();
  private static final UUID VACANCY_ID = UUID.randomUUID();
  private static final UUID STAGE_ID = UUID.randomUUID();
  private static final UUID APPLICATION_ID = UUID.randomUUID();

  @Test
  void shouldCreateApplication() {
    VacancyStage stage = VacancyStage.create(VACANCY_ID, "Applied",
            PipelineStageType.APPLIED, 0);
    VacancyStageJpaEntity jpaEntity = new VacancyStageJpaEntity();

    when(vacancyStageJpaRepository.findByVacancyIdOrderByOrderIndexAsc(VACANCY_ID))
            .thenReturn(List.of(jpaEntity));
    when(mapper.toDomain(jpaEntity)).thenReturn(stage);
    when(applicationRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

    ApplicationDto result = applicationService.createApplication(CANDIDATE_ID, VACANCY_ID);

    assertThat(result.getCandidateId()).isEqualTo(CANDIDATE_ID);
    assertThat(result.getVacancyId()).isEqualTo(VACANCY_ID);
    assertThat(result.getStatus()).isEqualTo(ApplicationStatus.ACTIVE);

    verify(applicationRepository).save(any());
  }

  @Test
  void shouldThrowWhenNoStagesForVacancy() {
    when(vacancyStageJpaRepository.findByVacancyIdOrderByOrderIndexAsc(VACANCY_ID))
            .thenReturn(List.of());

    assertThatThrownBy(() -> applicationService.createApplication(CANDIDATE_ID, VACANCY_ID))
            .isInstanceOf(BusinessException.class)
            .hasMessageContaining("No stages defined");
  }

  @Test
  void shouldGetApplication() {
    Application application = Application.builder()
            .id(APPLICATION_ID).candidateId(CANDIDATE_ID)
            .vacancyId(VACANCY_ID).currentStageId(STAGE_ID)
            .status(ApplicationStatus.ACTIVE)
            .createdAt(LocalDateTime.now()).build();

    when(applicationRepository.findById(APPLICATION_ID)).thenReturn(Optional.of(application));

    ApplicationDto result = applicationService.getApplication(APPLICATION_ID);

    assertThat(result.getId()).isEqualTo(APPLICATION_ID);
    assertThat(result.getStatus()).isEqualTo(ApplicationStatus.ACTIVE);
  }

  @Test
  void shouldThrowWhenApplicationNotFound() {
    when(applicationRepository.findById(APPLICATION_ID)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> applicationService.getApplication(APPLICATION_ID))
            .isInstanceOf(BusinessException.class)
            .hasMessageContaining("not found");
  }

  @Test
  void shouldMoveToStageAndPublishEvent() {
    Application application = Application.builder()
            .id(APPLICATION_ID).candidateId(CANDIDATE_ID)
            .vacancyId(VACANCY_ID).currentStageId(STAGE_ID)
            .status(ApplicationStatus.ACTIVE)
            .createdAt(LocalDateTime.now()).build();
    UUID newStageId = UUID.randomUUID();

    when(applicationRepository.findById(APPLICATION_ID)).thenReturn(Optional.of(application));
    when(applicationRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

    applicationService.moveToStage(APPLICATION_ID, newStageId);

    verify(applicationRepository).save(any());

    ArgumentCaptor<CandidateStageChangedEvent> captor =
            ArgumentCaptor.forClass(CandidateStageChangedEvent.class);
    verify(eventPublisher).publishEvent(captor.capture());

    CandidateStageChangedEvent event = captor.getValue();
    assertThat(event.getApplicationId()).isEqualTo(APPLICATION_ID);
    assertThat(event.getStageId()).isEqualTo(newStageId);
    assertThat(event.getCandidateId()).isEqualTo(CANDIDATE_ID);
  }

  @Test
  void shouldReturnKanbanBoard() {
    VacancyStageJpaEntity jpaEntity = new VacancyStageJpaEntity();
    VacancyStage stage = VacancyStage.builder()
            .id(STAGE_ID).vacancyId(VACANCY_ID)
            .name("Applied").type(PipelineStageType.APPLIED)
            .orderIndex(0).createdAt(LocalDateTime.now()).build();

    Application application = Application.builder()
            .id(APPLICATION_ID).candidateId(CANDIDATE_ID)
            .vacancyId(VACANCY_ID).currentStageId(STAGE_ID)
            .status(ApplicationStatus.ACTIVE)
            .createdAt(LocalDateTime.now()).build();

    when(vacancyStageJpaRepository.findByVacancyIdOrderByOrderIndexAsc(VACANCY_ID))
            .thenReturn(List.of(jpaEntity));
    when(mapper.toDomain(jpaEntity)).thenReturn(stage);
    when(applicationRepository.findByVacancyId(VACANCY_ID)).thenReturn(List.of(application));

    KanbanBoardDto board = applicationService.getKanbanBoard(VACANCY_ID);

    assertThat(board.getVacancyId()).isEqualTo(VACANCY_ID);
    assertThat(board.getColumns()).hasSize(1);
    assertThat(board.getColumns().getFirst().getStage().getName()).isEqualTo("Applied");
    assertThat(board.getColumns().getFirst().getApplications()).hasSize(1);
    assertThat(board.getColumns().getFirst().getApplications().getFirst().getId())
            .isEqualTo(APPLICATION_ID);
  }
}
