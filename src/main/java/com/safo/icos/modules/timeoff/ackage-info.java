/**
 * Time-off module.
 * Manages leave requests, approvals, balances, and calendar integration.
 *
 * <h3>Module layers</h3>
 * <ul>
 *   <li>{@code api} — DTOs and domain events.</li>
 *   <li>{@code domain} — {@code LeaveRequest}, {@code LeaveBalance},
 *       {@code LeaveType}, {@code LeaveAuditLog}.</li>
 *   <li>{@code application} — {@code LeaveService}, {@code LeaveCalendarListener}.</li>
 *   <li>{@code infrastructure} — JPA adapters.</li>
 * </ul>
 *
 * @see com.safo.icos.modules.timeoff.application.LeaveService
 */
package com.safo.icos.modules.timeoff;