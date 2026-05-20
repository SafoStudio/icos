package com.safo.icos.common.port.user;

import com.safo.icos.common.dto.MembershipDto;
import com.safo.icos.common.dto.UserDto;
import java.util.Optional;
import java.util.UUID;

public interface UserProvider {
    Optional<UserDto> findByEmail(String email);
    Optional<UserDto> findById(UUID userId);
    UserDto createUser(UserCreateData data);
    boolean existsByEmail(String email);
    void updateUserStatus(UUID userId, String status);
    Optional<MembershipDto> findActiveMembership(UUID userId, UUID companyId);
    Optional<MembershipDto> findFirstActiveMembershipByUserId(UUID userId);
    MembershipDto createMembership(UUID userId, UUID companyId, String role, UUID departmentId);
    Optional<MembershipDto> findMembershipById(UUID membershipId);

    /**
     * Finds full security context data for a user in a specific company.
     */
    Optional<UserPrincipalData> findPrincipal(UUID userId, UUID companyId);
}