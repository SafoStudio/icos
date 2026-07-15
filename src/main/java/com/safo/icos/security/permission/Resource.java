package com.safo.icos.security.permission;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a method parameter as the resource to check permissions against.
 * Used alongside {@link RequirePermission} to enable resource-based access control.
 *
 * <p>The annotated parameter's ID is extracted and the corresponding
 * entity is loaded from the database for policy evaluation.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Resource {
  /** The entity type to load from the repository. */
  Class<?> type();

  /** The name of the method parameter holding the resource ID. Default is "id". */
  String idSource() default "id";
}
