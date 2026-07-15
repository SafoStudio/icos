package com.safo.icos.modules.talent.recruitment.domain.model.enums;

/** Possible statuses of a job application. */
public enum ApplicationStatus {
  /** Application is active in the pipeline. */
  ACTIVE,
  /** Candidate has been hired. */
  HIRED,
  /** Candidate has been rejected. */
  REJECTED,
  /** Candidate withdrew their application. */
  WITHDRAWN
}
