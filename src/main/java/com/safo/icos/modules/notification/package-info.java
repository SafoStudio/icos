/**
 * Notification module.
 * Creates and manages user notifications in response to domain events
 * from other modules (recruitment, onboarding, leave, tasks, calendar).
 *
 * <h3>Module layers</h3>
 * <ul>
 *   <li>{@code domain} — {@code Notification} entity, {@code NotificationType}, repository port.</li>
 *   <li>{@code application} — {@code NotificationService}, {@code NotificationEventListener}.</li>
 *   <li>{@code infrastructure} — JPA adapters.</li>
 * </ul>
 *
 * @see com.safo.icos.modules.notification.application.NotificationService
 * @see com.safo.icos.modules.notification.application.listener.NotificationEventListener
 */
package com.safo.icos.modules.notification;