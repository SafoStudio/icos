package com.safo.icos.modules.analytics.presentation;

import com.safo.icos.modules.analytics.application.AnalyticsService;
import com.safo.icos.modules.analytics.application.dto.ExecutiveAnalyticsDto;
import com.safo.icos.modules.analytics.application.dto.RecruitmentAnalyticsDto;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

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
}
