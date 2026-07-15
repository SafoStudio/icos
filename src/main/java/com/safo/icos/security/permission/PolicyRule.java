package com.safo.icos.security.permission;

import com.safo.icos.security.context.IcosUserPrincipal;
import org.springframework.core.Ordered;

/**
 * Contract for a permission policy rule.
 * Implementations evaluate whether a user is allowed to perform
 * a given action on a resource.
 * <p>
 * Rules are ordered — higher priority (lower order value) rules
 * are evaluated first. If a rule returns {@link PolicyResult.Effect#ALLOW}
 * or {@link PolicyResult.Effect#DENY}, evaluation stops.
 * If a rule returns {@link PolicyResult.Effect#ABSTAIN},
 * the next rule in the chain is evaluated.
 */
public interface PolicyRule extends Ordered {

  /**
   * Checks whether this rule handles the given action.
   *
   * @param action the permission key (e.g., "user:edit")
   * @return {@code true} if this rule should evaluate the action
   */
  boolean supports(String action);

  /**
   * Evaluates the permission for the given user, action, and resource.
   *
   * @param user     the authenticated principal
   * @param action   the permission key
   * @param resource the resource being accessed (may be {@code null})
   * @return ALLOW, DENY, or ABSTAIN
   */
  PolicyResult check(IcosUserPrincipal user, String action, Object resource);

  /** Default priority is 100. Lower values have higher priority. */
  @Override
  default int getOrder() {
    return 100;
  }
}
