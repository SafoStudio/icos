package com.safo.icos.security.context;

/**
 * Thread-local holder for the current tenant (company) ID.
 * Set by {@link com.safo.icos.security.jwt.JwtAuthenticationFilter}
 * at the start of each request and cleared in a {@code finally} block.
 * <p>
 * Used by multi-tenant services to scope data access to a single company.
 */
public final class TenantContext {

  private static final ThreadLocal<String> currentTenant = new ThreadLocal<>();

  private TenantContext() {
  }

  /** Returns the current tenant ID, or {@code null} if not set. */
  public static String getTenantId() {
    return currentTenant.get();
  }

  /** Sets the tenant ID for the current thread. */
  public static void setTenantId(String tenantId) {
    currentTenant.set(tenantId);
  }

  /** Removes the tenant ID — must be called after each request to prevent leaks. */
  public static void clear() {
    currentTenant.remove();
  }
}