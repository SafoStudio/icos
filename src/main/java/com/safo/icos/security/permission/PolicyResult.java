package com.safo.icos.security.permission;

/**
 * Result of a permission policy evaluation.
 */
public record PolicyResult(Effect effect, String reason) {

  /** The rule explicitly allows the action. */
  public static PolicyResult allow() {
    return new PolicyResult(Effect.ALLOW, null);
  }

  /** The rule explicitly allows the action with a reason. */
  public static PolicyResult allow(String reason) {
    return new PolicyResult(Effect.ALLOW, reason);
  }

  /** The rule explicitly denies the action. */
  public static PolicyResult deny(String reason) {
    return new PolicyResult(Effect.DENY, reason);
  }

  /** The rule does not handle this action — evaluation continues. */
  public static PolicyResult abstain() {
    return new PolicyResult(Effect.ABSTAIN, null);
  }

  /** Possible outcomes of a policy rule evaluation. */
  public enum Effect {
    /** Access is explicitly granted. */
    ALLOW,
    /** Access is explicitly denied. */
    DENY,
    /** The rule does not apply — continue to the next rule. */
    ABSTAIN
  }
}
