package com.safo.icos.security.permission;

import com.safo.icos.security.context.IcosUserPrincipal;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

/**
 * Orchestrates permission evaluation across all registered {@link PolicyRule}s.
 * Rules are evaluated in order of priority. The first explicit ALLOW or DENY
 * stops the chain. If no rule matches, access is denied by default.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PermissionsService {

  private final List<PolicyRule> rules;

  /**
   * Asserts that the user is allowed to perform the given action
   * on the given resource.
   *
   * @param user     the authenticated principal
   * @param action   the permission key (e.g., "user:edit")
   * @param resource the resource being accessed, or {@code null}
   * @throws AccessDeniedException if no rule allows the action
   */
  public void assertCan(IcosUserPrincipal user, String action, Object resource) {
    StringBuilder trace = new StringBuilder("Trace: ");

    for (PolicyRule rule : rules) {
      if (rule.supports(action)) {
        PolicyResult result = rule.check(user, action, resource);
        trace.append(rule.getClass().getSimpleName())
                .append(":").append(result.effect()).append(" -> ");

        if (result.effect() == PolicyResult.Effect.DENY) {
          log.warn("Permission DENY for {} on {}. {}", user.getEmail(), action, trace);
          throw new AccessDeniedException("Access denied: " + result.reason());
        }
        if (result.effect() == PolicyResult.Effect.ALLOW) {
          log.debug("Permission ALLOW for {} on {}. {}", user.getEmail(), action, trace);
          return;
        }
      }
    }

    // Default to DENY if no rule allowed the action
    log.warn("Permission DENY (no ALLOW) for {} on {}. {}", user.getEmail(), action, trace);
    throw new AccessDeniedException("Access denied: No rule allowed this action");
  }
}
