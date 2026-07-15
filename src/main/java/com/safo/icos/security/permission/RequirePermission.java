package com.safo.icos.security.permission;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a method as requiring a specific permission.
 * Enforced by {@link SecurityAspect} via AOP.
 *
 * <p>Example: {@code @RequirePermission("user:edit")}
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RequirePermission {
  /** The permission key to check (e.g., "user:edit", "department:manage"). */
  String value();
}
