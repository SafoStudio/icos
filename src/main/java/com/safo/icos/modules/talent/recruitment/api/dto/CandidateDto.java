package com.safo.icos.modules.talent.recruitment.api.dto;

import java.util.UUID;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CandidateDto {
    private final UUID id;
    private final String firstName;
    private final String lastName;
    private final String email;
    private final String phone;
    private final String source;
    private final String resumeUrl;
}
