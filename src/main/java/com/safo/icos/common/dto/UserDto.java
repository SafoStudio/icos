package com.safo.icos.common.dto;

import lombok.Builder;
import lombok.Getter;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
public class UserDto {
    private final UUID id;
    private final String email;
    private final String firstName;
    private final String lastName;
    private final String status;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;
}