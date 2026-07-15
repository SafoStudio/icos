package com.safo.icos.modules.identity.user.api;

import com.safo.icos.modules.identity.user.api.dto.MembershipDto;
import com.safo.icos.modules.identity.user.api.dto.UserCreateDto;
import com.safo.icos.modules.identity.user.api.dto.UserDto;
import com.safo.icos.modules.identity.user.api.dto.UserPrincipalDto;

import java.util.Optional;
import java.util.UUID;

/**
 * Contract for user and membership management.
 * Provided by the {@code user} module and consumed by other modules
 * ({@code auth}, {@code invite}, {@code security}) that need access
 * to user data.
 */
public interface UserProvider {

  /**
   * Finds a user by their email address.
   *
   * @param email the user's email
   * @return the user if found
   */
  Optional<UserDto> findByEmail(String email);

  /**
   * Finds a user by their unique identifier.
   *
   * @param userId the user's UUID
   * @return the user if found
   */
  Optional<UserDto> findById(UUID userId);

  /**
   * Creates a new user.
   *
   * @param data user creation payload
   * @return the created user
   */
  UserDto createUser(UserCreateDto data);

  /**
   * Checks whether a user with the given email already exists.
   *
   * @param email the email to check
   * @return {@code true} if the email is already in use
   */
  boolean existsByEmail(String email);

  /**
   * Updates the status of a user (e.g., ACTIVE, INACTIVE, BLOCKED).
   *
   * @param userId the user's UUID
   * @param status the new status value
   */
  void updateUserStatus(UUID userId, String status);

  /**
   * Finds the manager (HEAD role) of a specific department.
   *
   * @param departmentId the department UUID
   * @return the manager if found
   */
  Optional<UserDto> findManagerByDepartmentId(UUID departmentId);

  /**
   * Finds an active membership for a user in a specific company.
   *
   * @param userId    the user's UUID
   * @param companyId the company UUID
   * @return the membership if found and active
   */
  Optional<MembershipDto> findActiveMembership(UUID userId, UUID companyId);

  /**
   * Finds the first active membership for a user across all companies.
   *
   * @param userId the user's UUID
   * @return the first active membership found
   */
  Optional<MembershipDto> findFirstActiveMembershipByUserId(UUID userId);

  /**
   * Creates a new membership linking a user to a company with a specific role.
   *
   * @param userId       the user's UUID
   * @param companyId    the company UUID
   * @param roleId       the role UUID
   * @param departmentId optional department assignment
   * @param managerId    optional manager assignment
   * @return the created membership
   */
  MembershipDto createMembership(UUID userId, UUID companyId, UUID roleId,
                                 UUID departmentId, UUID managerId);

  /**
   * Finds a membership by its unique identifier.
   *
   * @param membershipId the membership UUID
   * @return the membership if found
   */
  Optional<MembershipDto> findMembershipById(UUID membershipId);

  /**
   * Finds full security context data for a user in a specific company.
   * Used by the JWT authentication filter to construct the
   * {@link com.safo.icos.security.context.IcosUserPrincipal}.
   *
   * @param userId    the user's UUID
   * @param companyId the company (tenant) UUID
   * @return the principal data if found
   */
  Optional<UserPrincipalDto> findPrincipal(UUID userId, UUID companyId);
}
