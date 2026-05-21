package com.safo.icos.common.port.user;

import java.util.UUID;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserCreateData {
  private final String email;
  private final String firstName;
  private final String lastName;
  private final UUID createdBy;
}