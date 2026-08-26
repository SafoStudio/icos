/**
 * Custom field module.
 * Provides a flexible framework for dynamically extending entities
 * with user-defined fields without schema changes.
 * <p>
 * Use cases include:
 * <ul>
 *   <li>Adding extra fields to recruitment candidate forms.</li>
 *   <li>Extending user profiles with custom attributes.</li>
 *   <li>Defining company-specific fields for departments, positions, or jobs.</li>
 * </ul>
 * <p>
 * Supported field types: STRING, NUMBER, DATE, BOOLEAN, ENUM, ENTITY_REF.
 * Definitions can be marked as required, filterable, and support enum options.
 */
package com.safo.icos.modules.customfield;