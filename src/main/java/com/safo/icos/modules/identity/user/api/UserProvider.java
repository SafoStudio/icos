package com.safo.icos.modules.user.api;

import com.safo.icos.modules.user.api.dto.MembershipDto;
import com.safo.icos.modules.user.api.dto.UserCreateDto;
import com.safo.icos.modules.user.api.dto.UserDto;
import com.safo.icos.modules.user.api.dto.UserPrincipalDto;
import java.util.Optional;
import java.util.UUID;

public interface UserProvider {
  Optional<UserDto> findByEmail(String email);

  Optional<UserDto> findById(UUID userId);

  UserDto createUser(UserCreateDto data);

  boolean existsByEmail(String email);

  void updateUserStatus(UUID userId, String status);

  /**
   * Finds the manager (HEAD role) of a specific department.
   */
  Optional<UserDto> findManagerByDepartmentId(UUID departmentId);

  Optional<MembershipDto> findActiveMembership(UUID userId, UUID companyId);

  Optional<MembershipDto> findFirstActiveMembershipByUserId(UUID userId);

  MembershipDto createMembership(UUID userId, UUID companyId, UUID roleId, UUID departmentId, UUID managerId);

  Optional<MembershipDto> findMembershipById(UUID membershipId);

  /**
   * Finds full security context data for a user in a specific company.
   */
  Optional<UserPrincipalDto> findPrincipal(UUID userId, UUID companyId);
}
