package com.safo.icos.modules.talent.recruitment.api.dto;

import java.util.UUID;
import lombok.Builder;
import lombok.Getter;

/**
 * DTO representing a job candidate.
 */
@Getter
@Builder
public class CandidateDto {
  private final UUID id;
  private final String firstName;
  private final String lastName;
  private final String email;
  private final String phone;
  /** Source of the candidate (e.g., LinkedIn, referral, job board). */
  private final String source;
  /** URL to the candidate's uploaded resume. */
  private final String resumeUrl;
}