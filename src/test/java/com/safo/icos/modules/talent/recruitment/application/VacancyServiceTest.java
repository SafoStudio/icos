package com.safo.icos.modules.talent.recruitment.application;

import com.safo.icos.common.exception.BusinessException;
import com.safo.icos.modules.talent.recruitment.application.dto.CreateVacancyRequest;
import com.safo.icos.modules.talent.recruitment.application.dto.VacancyDto;
import com.safo.icos.modules.talent.recruitment.domain.model.VacancyStage;
import com.safo.icos.modules.talent.recruitment.domain.repository.VacancyRepository;
import com.safo.icos.modules.talent.recruitment.infrastructure.mapper.RecruitmentMapper;
import com.safo.icos.modules.talent.recruitment.infrastructure.repository.entity.VacancyStageJpaEntity;
import com.safo.icos.modules.talent.recruitment.infrastructure.repository.jpa.VacancyStageJpaRepository;
import com.safo.icos.security.context.IcosUserPrincipal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class VacancyServiceTest {

  @Mock private VacancyRepository vacancyRepository;
  @Mock private VacancyStageJpaRepository vacancyStageJpaRepository;
  @Mock private RecruitmentMapper mapper;
  @Mock private Authentication authentication;

  @InjectMocks
  private VacancyService vacancyService;

  private static final UUID COMPANY_ID = UUID.randomUUID();
  private static final UUID USER_ID = UUID.randomUUID();

  @BeforeEach
  void setUp() {
    SecurityContextHolder.getContext().setAuthentication(authentication);
  }

  @Test
  void shouldCreateVacancyWithDefaultStages() {
    IcosUserPrincipal principal = IcosUserPrincipal.builder()
            .userId(USER_ID).email("admin@test.com")
            .roleName("ADMIN").authorities(List.of()).build();
    when(authentication.getPrincipal()).thenReturn(principal);

    CreateVacancyRequest request = new CreateVacancyRequest();
    request.setCompanyId(COMPANY_ID);
    request.setDepartmentId(UUID.randomUUID());
    request.setPositionId(UUID.randomUUID());
    request.setTitle("Java Developer");
    request.setDescription("Backend role");
    request.setRequirements("Java, Spring");

    when(vacancyRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
    when(mapper.toEntity(any(VacancyStage.class))).thenReturn(new VacancyStageJpaEntity());
    when(vacancyStageJpaRepository.save(any())).thenReturn(new VacancyStageJpaEntity());

    VacancyDto result = vacancyService.createVacancy(request);

    assertThat(result.getTitle()).isEqualTo("Java Developer");
    assertThat(result.getCompanyId()).isEqualTo(COMPANY_ID);

    // Verify 5 default stages were created
    verify(mapper, times(5)).toEntity(any(VacancyStage.class));
    verify(vacancyStageJpaRepository, times(5)).save(any());
  }

  @Test
  void shouldThrowWhenVacancyNotFound() {
    UUID vacancyId = UUID.randomUUID();
    when(vacancyRepository.findById(vacancyId)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> vacancyService.getVacancy(vacancyId))
            .isInstanceOf(BusinessException.class)
            .hasMessageContaining("not found");
  }
}