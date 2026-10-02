package com.safo.icos.modules.analytics.presentation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.safo.icos.modules.analytics.application.AnalyticsService;
import com.safo.icos.modules.analytics.application.dto.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

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

  @Test
  void shouldReturnHeadcountTrend() {
    HeadcountPointDto point = HeadcountPointDto.builder()
        .period("2026-09")
        .headcount(150)
        .hired(5)
        .fired(1)
        .turnoverRate(0.7)
        .build();
    when(analyticsService.getHeadcountTrend(12, null)).thenReturn(List.of(point));

    List<HeadcountPointDto> result = resolver.headcountTrend(12, null);

    assertThat(result).hasSize(1);
    assertThat(result.get(0).getPeriod()).isEqualTo("2026-09");
    assertThat(result.get(0).getTurnoverRate()).isEqualTo(0.7);
    verify(analyticsService).getHeadcountTrend(12, null);
  }

  @Test
  void shouldReturnTimeOffHeatmap() {
    TimeOffDayDto day = TimeOffDayDto.builder()
        .date("2026-10-01")
        .departmentName("Engineering")
        .absentCount(2)
        .totalCount(20)
        .loadPercent(10.0)
        .build();
    when(analyticsService.getTimeOffHeatmap("2026-10-01", "2026-10-02", null)).thenReturn(List.of(day));

    List<TimeOffDayDto> result = resolver.timeOffHeatmap("2026-10-01", "2026-10-02", null);

    assertThat(result).hasSize(1);
    assertThat(result.get(0).getAbsentCount()).isEqualTo(2);
    verify(analyticsService).getTimeOffHeatmap("2026-10-01", "2026-10-02", null);
  }

  @Test
  void shouldReturnSalaryDistribution() {
    SalaryBucketDto bucket = SalaryBucketDto.builder()
        .label("Junior")
        .min(1000)
        .max(2000)
        .avg(1500)
        .count(10)
        .build();
    when(analyticsService.getSalaryDistribution(null)).thenReturn(List.of(bucket));

    List<SalaryBucketDto> result = resolver.salaryDistribution(null);

    assertThat(result).hasSize(1);
    assertThat(result.get(0).getLabel()).isEqualTo("Junior");
    verify(analyticsService).getSalaryDistribution(null);
  }

  @Test
  void shouldReturnObjectiveProgress() {
    ObjectiveProgressDto obj = ObjectiveProgressDto.builder()
        .id(UUID.randomUUID())
        .title("Scale SLA")
        .progress(85.0)
        .keyResults(List.of())
        .build();
    when(analyticsService.getObjectiveProgress(null, "Q3")).thenReturn(List.of(obj));

    List<ObjectiveProgressDto> result = resolver.objectiveProgress(null, "Q3");

    assertThat(result).hasSize(1);
    assertThat(result.get(0).getTitle()).isEqualTo("Scale SLA");
    verify(analyticsService).getObjectiveProgress(null, "Q3");
  }
}
