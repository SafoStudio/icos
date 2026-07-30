package com.safo.icos.modules.talent.onboarding.application;

import com.safo.icos.modules.talent.onboarding.domain.model.OnboardingProcess;
import com.safo.icos.modules.talent.onboarding.domain.model.OnboardingTask;
import com.safo.icos.modules.talent.onboarding.domain.model.OnboardingTaskTemplate;
import com.safo.icos.modules.talent.onboarding.domain.model.OnboardingTemplate;
import com.safo.icos.modules.talent.onboarding.domain.model.enums.OnboardingTaskStatus;
import com.safo.icos.modules.talent.onboarding.domain.repository.OnboardingTaskTemplateRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class OnboardingServiceIntegrationTest {

  @Autowired private OnboardingService onboardingService;
  @Autowired private OnboardingTaskTemplateRepository taskTemplateRepository;

  private static final UUID COMPANY_ID = UUID.fromString("e4b10b0e-3fa1-4a4e-98ee-5fb9d2a3ea61");
  private static final UUID MEMBERSHIP_ID = UUID.fromString("a1b2c3d4-e5f6-4a7b-8c9d-0e1f2a3b4c5d");

  @Test
  void shouldCompleteFullOnboardingFlow() {
    OnboardingTemplate template = onboardingService.createTemplate(
            COMPANY_ID, "IT Onboarding", "Standard IT setup");

    taskTemplateRepository.save(
            OnboardingTaskTemplate.create(template.getId(), "Setup laptop", "Install required software", 1));
    taskTemplateRepository.save(
            OnboardingTaskTemplate.create(template.getId(), "Create accounts", "Email, Slack, GitHub", 2));
    taskTemplateRepository.save(
            OnboardingTaskTemplate.create(template.getId(), "Complete HR paperwork", "Sign NDA and contract", 3));

    LocalDate startDate = LocalDate.of(2026, 8, 1);
    OnboardingProcess process = onboardingService.startOnboarding(
            MEMBERSHIP_ID, template.getId(), startDate);

    assertThat(process)
            .isNotNull()
            .satisfies(p -> {
              assertThat(p.getId()).isNotNull();
              assertThat(p.getMembershipId()).isEqualTo(MEMBERSHIP_ID);
              assertThat(p.getTemplateId()).isEqualTo(template.getId());
              assertThat(p.getStartDate()).isEqualTo(startDate);
              assertThat(p.getTargetCompletionDate()).isEqualTo(startDate.plusWeeks(2));
            });

    List<OnboardingTask> tasks = onboardingService.getProcessTasks(process.getId());
    assertThat(tasks)
            .hasSize(3)
            .allMatch(t -> t.getStatus() == OnboardingTaskStatus.PENDING);

    onboardingService.completeTask(tasks.get(0).getId());
    onboardingService.completeTask(tasks.get(1).getId());

    List<OnboardingTask> remainingTasks = onboardingService.getProcessTasks(process.getId());
    assertThat(remainingTasks.stream().filter(t -> t.getStatus() == OnboardingTaskStatus.DONE))
            .hasSize(2);

    onboardingService.completeTask(tasks.get(2).getId());

    List<OnboardingTask> finalTasks = onboardingService.getProcessTasks(process.getId());
    assertThat(finalTasks)
            .isNotEmpty()
            .allMatch(t -> t.getStatus() == OnboardingTaskStatus.DONE);
  }

  @Test
  void shouldCreateTemplateAndRetrieveTaskTemplates() {
    OnboardingTemplate template = onboardingService.createTemplate(
            COMPANY_ID, "HR Onboarding", "HR department setup");

    taskTemplateRepository.save(
            OnboardingTaskTemplate.create(template.getId(), "Benefits enrollment", "Choose health plan", 5));
    taskTemplateRepository.save(
            OnboardingTaskTemplate.create(template.getId(), "Policy review", "Read company policies", 2));

    List<OnboardingTaskTemplate> templates = taskTemplateRepository.findByTemplateId(template.getId());

    assertThat(templates)
            .hasSize(2)
            .extracting(OnboardingTaskTemplate::getTitle)
            .containsExactlyInAnyOrder("Benefits enrollment", "Policy review");
  }
}