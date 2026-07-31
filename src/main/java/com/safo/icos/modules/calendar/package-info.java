/**
 * Calendar module.
 * Manages user calendar events — creation, date-range queries,
 * and event notifications.
 *
 * <h3>Module layers</h3>
 * <ul>
 *   <li>{@code api} — DTOs and domain events.</li>
 *   <li>{@code domain} — {@code Event} entity, enums, repository port.</li>
 *   <li>{@code application} — {@code CalendarService}.</li>
 *   <li>{@code infrastructure} — JPA adapters.</li>
 * </ul>
 *
 * @see com.safo.icos.modules.calendar.application.CalendarService
 */
package com.safo.icos.modules.calendar;