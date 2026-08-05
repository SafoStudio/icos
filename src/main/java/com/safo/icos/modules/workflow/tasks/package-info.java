/**
 * Workflow tasks module.
 * Manages task creation, assignment, and status transitions
 * within departments.
 *
 * <h3>Module layers</h3>
 * <ul>
 *   <li>{@code api} — DTOs and domain events.</li>
 *   <li>{@code domain} — {@code Task}, {@code TaskStatus}, repository port.</li>
 *   <li>{@code application} — {@code TaskService}.</li>
 *   <li>{@code infrastructure} — JPA adapters.</li>
 * </ul>
 *
 * @see com.safo.icos.modules.workflow.tasks.application.TaskService
 */
package com.safo.icos.modules.workflow.tasks;