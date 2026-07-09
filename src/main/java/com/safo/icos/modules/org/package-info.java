/**
 * Organisation management module.
 * Manages companies, departments, and job positions.
 * Provides services for creating and querying organisational structures
 * used by the identity and invite modules.
 *
 * <h3>Module layers</h3>
 * <ul>
 *   <li>{@code api} — error codes.</li>
 *   <li>{@code domain} — {@code Company}, {@code Department}, {@code Position} entities.</li>
 *   <li>{@code application} — {@code CompanyService}, {@code DepartmentService}, {@code PositionService}.</li>
 *   <li>{@code infrastructure} — JPA adapters.</li>
 * </ul>
 *
 * @see com.safo.icos.modules.org.application.CompanyService
 * @see com.safo.icos.modules.org.application.DepartmentService
 * @see com.safo.icos.modules.org.application.PositionService
 */
package com.safo.icos.modules.org;