package com.safo.icos.modules.talent.onboarding.api.dto;

import lombok.Builder;
import lombok.Getter;
import java.time.LocalDate;
import java.util.UUID;

@Getter
@Builder
public class OnboardingProcessDto {
    private final UUID id;
    private final UUID membershipId;
    private final String status;
    private final LocalDate startDate;
}
