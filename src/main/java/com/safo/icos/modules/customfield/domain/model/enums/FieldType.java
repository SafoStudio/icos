package com.safo.icos.modules.customfield.domain.model.enums;

/**
 * Supported custom field data types.
 */
public enum FieldType {
  /** Single-line text input (e.g. city, short code, title). */
  STRING,
  /** Multi-line text area (e.g. notes, feedback, candidate summary). */
  MULTILINE,
  /** Numeric value (e.g. salary expectation, years of experience, score). */
  NUMBER,
  /** Date and time value (e.g. desired start date, interview deadline). */
  DATE,
  /** Boolean toggle (e.g. relocation agreement, visa sponsorship needed). */
  BOOLEAN,
  /** Single-choice enumeration from a predefined list of options. */
  ENUM,
  /** Multiple-choice selection from a predefined list of options. */
  MULTI_SELECT,
  /** Web URL input with link validation (e.g. portfolio, LinkedIn profile). */
  URL,
  /** Reference to an existing entity by UUID and entity type. */
  ENTITY_REF
}