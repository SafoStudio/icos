/**
 * Security module.
 * Provides JWT-based authentication, multi-tenant context,
 * and attribute-based access control via policy rules.
 *
 * <h3>Packages</h3>
 * <ul>
 *   <li>{@code config} — Spring Security configuration.</li>
 *   <li>{@code context} — {@code IcosUserPrincipal} and {@code TenantContext}.</li>
 *   <li>{@code jwt} — JWT generation, validation, and authentication filter.</li>
 *   <li>{@code permission} — AOP-based permission enforcement with pluggable policy rules.</li>
 * </ul>
 *
 * @see com.safo.icos.security.config.SecurityConfig
 * @see com.safo.icos.security.jwt.JwtAuthenticationFilter
 * @see com.safo.icos.security.permission.SecurityAspect
 */
package com.safo.icos.security;
