/**
 * Recruitment module (Applicant Tracking System).
 * Manages vacancies, candidates, applications, and the hiring pipeline.
 *
 * <h3>Module layers</h3>
 * <ul>
 *   <li>{@code api} — DTOs, error codes, and domain events.</li>
 *   <li>{@code domain} — {@code Vacancy}, {@code Candidate}, {@code Application}, {@code VacancyStage}.</li>
 *   <li>{@code application} — {@code VacancyService}, {@code CandidateService}, {@code ApplicationService}.</li>
 *   <li>{@code infrastructure} — JPA adapters, mappers.</li>
 * </ul>
 *
 * @see com.safo.icos.modules.talent.recruitment.application.VacancyService
 * @see com.safo.icos.modules.talent.recruitment.application.ApplicationService
 */
package com.safo.icos.modules.talent.recruitment;