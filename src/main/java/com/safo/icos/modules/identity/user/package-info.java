/**
 * User management module.
 * <p>
 * Manages user accounts, profiles, memberships, and documents.
 * Provides the {@link com.safo.icos.modules.identity.user.api.UserProvider}
 * contract for other modules ({@code auth}, {@code invite}, {@code security})
 * to query and manage user data.
 * <p>
 * <h3>Module layers</h3>
 * <ul>
 *   <li>{@code api} — contracts and DTOs for other modules.</li>
 *   <li>{@code domain} — {@code User}, {@code Membership}, value objects, domain services.</li>
 *   <li>{@code application} — {@code UserService} orchestrating user operations.</li>
 *   <li>{@code infrastructure} — JPA adapters, profile and document repositories.</li>
 * </ul>
 *
 * @see com.safo.icos.modules.identity.user.api.UserProvider
 * @see com.safo.icos.modules.identity.user.application.service.UserService
 * @see com.safo.icos.modules.identity.user.domain.model.User
 */
package com.safo.icos.modules.identity.user;