/**
 * Onboarding module.
 * Manages the employee onboarding process after hiring —
 * templates, task instantiation, and completion tracking.
 *
 * <h3>Module layers</h3>
 * <ul>
 *   <li>{@code api} — DTOs, error codes, and domain events.</li>
 *   <li>{@code domain} — {@code OnboardingProcess}, {@code OnboardingTask},
 *       {@code OnboardingTemplate}, {@code OnboardingTaskTemplate}.</li>
 *   <li>{@code application} — {@code OnboardingService}.</li>
 *   <li>{@code infrastructure} — JPA adapters.</li>
 * </ul>
 *
 * @see com.safo.icos.modules.talent.onboarding.application.OnboardingService
 */
package com.safo.icos.modules.talent.onboarding;