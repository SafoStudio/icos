package com.safo.icos.modules.talent.recruitment.domain.model.enums;

/** Standard types of recruitment pipeline stages. */
public enum PipelineStageType {
  /** Initial application received. */
  APPLIED,
  /** Resume/phone screening. */
  SCREENING,
  /** Technical assessment/interview. */
  TECHNICAL,
  /** HR or culture-fit interview. */
  HR,
  /** Offer stage. */
  OFFER,
  /** Candidate hired. */
  HIRED,
  /** Candidate rejected. */
  REJECTED
}