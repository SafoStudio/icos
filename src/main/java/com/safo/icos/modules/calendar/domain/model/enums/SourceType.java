package com.safo.icos.modules.calendar.domain.model.enums;

/** Source that created the calendar event. */
public enum SourceType {
  /** Created by HR department. */
  HR,
  /** Created from recruitment pipeline. */
  RECRUITING,
  /** System-generated event. */
  SYSTEM,
  /** Manually created by a user. */
  MANUAL
}