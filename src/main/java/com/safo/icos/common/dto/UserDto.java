package com.safo.icos.common.dto;

import lombok.Builder;
import lombok.Getter;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Builder
public class UserDto {
    private final UUID id;
    private final String email;
    private final String firstName;
    private final String lastName;
    private final String companyId;
    private final String role;
    private final UUID departmentId;
    private final List<String> permissions;
    private final String status;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;
}