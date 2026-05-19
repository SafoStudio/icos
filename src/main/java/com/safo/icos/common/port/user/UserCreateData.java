package com.safo.icos.common.port.user;

import lombok.Builder;
import lombok.Getter;
import java.util.UUID;

@Getter
@Builder
public class UserCreateData {
    private final String email;
    private final String firstName;
    private final String lastName;
    private final UUID createdBy;
}