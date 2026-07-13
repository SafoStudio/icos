package com.safo.icos.modules.talent.recruitment.domain.model.enums;

/** Possible statuses of a vacancy. */
public enum VacancyStatus {
  /** Vacancy is being prepared, not yet published. */
  DRAFT,
  /** Vacancy is open for applications. */
  OPEN,
  /** Vacancy has been closed. */
  CLOSED,
  /** Vacancy is temporarily paused. */
  PAUSED
}