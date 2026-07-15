package com.safo.icos.seeder.dto;

import java.util.UUID;
import lombok.Data;

@Data
public class UserSeedDto {
  private UUID id;
  private String email;
  private String firstName;
  private String lastName;
  private String status;
  private UUID createdBy;
  private String password;

  // Profile fields
  private String phoneNumber;
  private String address;
  private String hireDate;
  private String birthDate;
  private String avatarPath;

  private String createdAt;
  private String updatedAt;
}
