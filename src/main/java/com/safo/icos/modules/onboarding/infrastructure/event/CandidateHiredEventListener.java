package com.safo.icos.modules.onboarding.infrastructure.event;

import com.safo.icos.modules.onboarding.application.OnboardingService;
import com.safo.icos.modules.onboarding.domain.model.OnboardingTemplate;
import com.safo.icos.modules.onboarding.domain.repository.OnboardingTemplateRepository;
import com.safo.icos.modules.recruitment.domain.event.CandidateHiredEvent;
import com.safo.icos.common.port.user.UserProvider;
import com.safo.icos.common.dto.MembershipDto;
import java.time.LocalDate;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class CandidateHiredEventListener {
    private final OnboardingService onboardingService;
    private final OnboardingTemplateRepository templateRepository;
    private final UserProvider userProvider;

    @EventListener
    public void onCandidateHired(CandidateHiredEvent event) {
        log.info("Received CandidateHiredEvent for user: {}", event.getUserId());

        // 1. Find the membership for the new user in the company
        Optional<MembershipDto> membership = userProvider.findActiveMembership(event.getUserId(), event.getCompanyId());

        if (membership.isEmpty()) {
            log.warn("Membership not found for hired candidate {} in company {}", event.getUserId(), event.getCompanyId());
            return;
        }

        // 2. Find a default onboarding template for the company
        Optional<OnboardingTemplate> template = templateRepository.findByCompanyId(event.getCompanyId())
                .stream()
                .findFirst();

        if (template.isPresent()) {
            onboardingService.startOnboarding(
                    membership.get().getId(),
                    template.get().getId(),
                    LocalDate.now().plusDays(1) // Start tomorrow
            );
            log.info("Started onboarding for membership: {}", membership.get().getId());
        } else {
            log.warn("No onboarding template found for company: {}", event.getCompanyId());
        }
    }
}
