package com.safo.icos.modules.analytics.presentation;

import com.safo.icos.modules.analytics.application.AnalyticsService;
import com.safo.icos.modules.analytics.application.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.List;
import java.util.UUID;

@Controller
@RequiredArgsConstructor
public class AnalyticsGraphQLResolver {

  private final AnalyticsService analyticsService;

  @QueryMapping
  public ExecutiveAnalyticsDto executiveAnalytics() {
    return analyticsService.getExecutiveAnalytics();
  }

  @QueryMapping
  public RecruitmentAnalyticsDto recruitmentAnalytics() {
    return analyticsService.getRecruitmentAnalytics();
  }

  @QueryMapping
  public List<HeadcountPointDto> headcountTrend(
      @Argument Integer months,
      @Argument UUID departmentId
  ) {
    return analyticsService.getHeadcountTrend(months, departmentId);
  }

  @QueryMapping
  public List<DepartmentHeadcountDto> headcountByDepartment(@Argument Integer months) {
    return analyticsService.getHeadcountByDepartment(months);
  }

  @QueryMapping
  public List<TimeOffDayDto> timeOffHeatmap(
      @Argument String from,
      @Argument String to,
      @Argument UUID departmentId
  ) {
    return analyticsService.getTimeOffHeatmap(from, to, departmentId);
  }

  @QueryMapping
  public List<SalaryBucketDto> salaryDistribution(@Argument UUID departmentId) {
    return analyticsService.getSalaryDistribution(departmentId);
  }

  @QueryMapping
  public List<BonusPointDto> bonusDynamics(@Argument Integer months) {
    return analyticsService.getBonusDynamics(months);
  }

  @QueryMapping
  public List<ObjectiveProgressDto> objectiveProgress(
      @Argument UUID teamId,
      @Argument String period
  ) {
    return analyticsService.getObjectiveProgress(teamId, period);
  }
}
