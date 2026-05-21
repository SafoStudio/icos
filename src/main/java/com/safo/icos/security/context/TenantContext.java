package com.safo.icos.security.context;

public final class TenantContext {
  private static final ThreadLocal<String> currentTenant = new ThreadLocal<>();

  private TenantContext() {
  }

  public static String getTenantId() {
    return currentTenant.get();
  }

  public static void setTenantId(String tenantId) {
    currentTenant.set(tenantId);
  }

  public static void clear() {
    currentTenant.remove();
  }
}