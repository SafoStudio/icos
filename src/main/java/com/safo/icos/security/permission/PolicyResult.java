package com.safo.icos.security.permission;

public record PolicyResult(Effect effect, String reason) {
  public static PolicyResult allow() {
    return new PolicyResult(Effect.ALLOW, null);
  }

  public static PolicyResult allow(String reason) {
    return new PolicyResult(Effect.ALLOW, reason);
  }

  public static PolicyResult deny(String reason) {
    return new PolicyResult(Effect.DENY, reason);
  }

  public static PolicyResult abstain() {
    return new PolicyResult(Effect.ABSTAIN, null);
  }

  public enum Effect { ALLOW, DENY, ABSTAIN }
}