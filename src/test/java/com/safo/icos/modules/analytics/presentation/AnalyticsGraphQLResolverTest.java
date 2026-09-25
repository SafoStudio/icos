package com.safo.icos.modules.analytics.presentation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.safo.icos.modules.analytics.application.AnalyticsService;
import com.safo.icos.modules.analytics.application.dto.ExecutiveAnalyticsDto;
import com.safo.icos.modules.analytics.application.dto.RecruitmentAnalyticsDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AnalyticsGraphQLResolverTest {

  @Mock
  private AnalyticsService analyticsService;

  @InjectMocks
  private AnalyticsGraphQLResolver resolver;

  @Test
  void shouldReturnExecutiveAnalytics() {
    ExecutiveAnalyticsDto expected = ExecutiveAnalyticsDto.builder()
        .totalEmployees(156)
        .activeProjects(8)
        .turnoverRate(3.2)
        .monthlyPayroll(284500.0)
        .overallOkrProgress(78.0)
        .build();

    when(analyticsService.getExecutiveAnalytics()).thenReturn(expected);

    ExecutiveAnalyticsDto result = resolver.executiveAnalytics();

    assertThat(result).isNotNull();
    assertThat(result.getTotalEmployees()).isEqualTo(156);
    assertThat(result.getTurnoverRate()).isEqualTo(3.2);
    assertThat(result.getOverallOkrProgress()).isEqualTo(78.0);
    verify(analyticsService).getExecutiveAnalytics();
  }

  @Test
  void shouldReturnRecruitmentAnalytics() {
    RecruitmentAnalyticsDto expected = RecruitmentAnalyticsDto.builder()
        .openVacancies(8)
        .totalCandidates(142)
        .avgTimeToHireDays(24)
        .offerAcceptanceRate(88.0)
        .build();

    when(analyticsService.getRecruitmentAnalytics()).thenReturn(expected);

    RecruitmentAnalyticsDto result = resolver.recruitmentAnalytics();

    assertThat(result).isNotNull();
    assertThat(result.getOpenVacancies()).isEqualTo(8);
    assertThat(result.getTotalCandidates()).isEqualTo(142);
    assertThat(result.getOfferAcceptanceRate()).isEqualTo(88.0);
    verify(analyticsService).getRecruitmentAnalytics();
  }
}
