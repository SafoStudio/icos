package com.safo.icos.common.port.user;

import lombok.Builder;
import lombok.Getter;
import java.util.Set;
import java.util.UUID;

@Getter
@Builder
public class UserPrincipalData {
    private final UUID userId;
    private final String email;
    private final UUID membershipId;
    private final UUID companyId;
    private final String roleName;
    private final UUID departmentId;
    private final String departmentName;
    private final Set<String> permissions;
}
