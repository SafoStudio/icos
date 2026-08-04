/**
 * Approvals workflow module.
 * Generic approval system that can be attached to any entity
 * (leave requests, expenses, etc.) via {@code targetEntityId}
 * and {@code targetEntityType}.
 *
 * <h3>Module layers</h3>
 * <ul>
 *   <li>{@code api} — DTOs and domain events.</li>
 *   <li>{@code domain} — {@code ApprovalRequest}, {@code ApprovalStatus}, repository port.</li>
 *   <li>{@code application} — {@code ApprovalService}.</li>
 *   <li>{@code infrastructure} — JPA adapters.</li>
 * </ul>
 *
 * @see com.safo.icos.modules.workflow.approvals.application.ApprovalService
 */
package com.safo.icos.modules.workflow.approvals;