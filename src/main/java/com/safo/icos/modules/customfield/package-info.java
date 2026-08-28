/**
 * Dynamic Custom Field Module.
 * <p>
 * Provides a scalable and flexible framework for dynamically extending domain entities
 * (Candidates, Onboarding workflows, User profiles, Jobs, Departments, Positions, Companies)
 * with user-defined custom fields without database schema alterations.
 * <p>
 * Key capabilities:
 * <ul>
 *   <li>Rich field data types: STRING, MULTILINE, NUMBER, DATE, BOOLEAN, ENUM, MULTI_SELECT, URL, ENTITY_REF.</li>
 *   <li>Advanced validation rules: required constraints, regex patterns, numerical bounds (min/max), and choice constraints.</li>
 *   <li>Conditional visibility: rules dynamically displaying fields based on parent field answers.</li>
 *   <li>Dual Presentation Layer: both GraphQL and RESTful APIs with role-based security permissions.</li>
 *   <li>Batch persistence support for efficient multi-field form updates.</li>
 * </ul>
 */
package com.safo.icos.modules.customfield;