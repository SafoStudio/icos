package com.safo.icos.security.context;

import java.util.Collection;
import java.util.UUID;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * Custom {@link UserDetails} implementation holding the authenticated user's
 * security context.
 * Populated by {@link com.safo.icos.security.jwt.JwtAuthenticationFilter}
 * from JWT claims and database lookups.
 */
@Getter
@Builder
public class IcosUserPrincipal implements UserDetails {
  /** User's unique identifier. */
  private final UUID userId;
  /** User's email — also serves as the username. */
  private final String email;
  /** Current membership ID. */
  private final UUID membershipId;
  /** Tenant ID — the company the user is acting within. */
  private final UUID companyId;
  /** Role name within the current company (OWNER, ADMIN, HEAD, SPECIALIST). */
  private final String roleName;
  /** Department ID (optional). */
  private final UUID departmentId;
  /** Department name (optional). */
  private final String departmentName;
  /** Granted authorities derived from role permissions. */
  private final Collection<? extends GrantedAuthority> authorities;

  @Override
  public @NonNull Collection<? extends GrantedAuthority> getAuthorities() {
    return authorities;
  }

  /** Password is managed separately — not stored in the principal. */
  @Override
  public String getPassword() {
    return null;
  }

  /** Username is the user's email. */
  @Override
  public @NonNull String getUsername() {
    return email;
  }

  /** Account expiration is not used — always {@code true}. */
  @Override
  public boolean isAccountNonExpired() {
    return true;
  }

  /** Account locking is managed via {@code UserStatus} — always {@code true} here. */
  @Override
  public boolean isAccountNonLocked() {
    return true;
  }

  /** Credential expiration is not used — always {@code true}. */
  @Override
  public boolean isCredentialsNonExpired() {
    return true;
  }

  /** Disabled users are handled at the authentication level — always {@code true} here. */
  @Override
  public boolean isEnabled() {
    return true;
  }
}