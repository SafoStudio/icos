package com.safo.icos.seeder;

import com.safo.icos.modules.identity.auth.infrastructure.repository.entity.AuthCredentialsJpaEntity;
import com.safo.icos.modules.identity.auth.infrastructure.repository.jpa.AuthCredentialsJpaRepository;
import com.safo.icos.modules.identity.user.infrastructure.repository.entity.UserJpaEntity;
import com.safo.icos.modules.identity.user.infrastructure.repository.entity.UserProfileJpaEntity;
import com.safo.icos.modules.identity.user.infrastructure.repository.jpa.UserJpaRepository;
import com.safo.icos.modules.identity.user.infrastructure.repository.jpa.UserProfileJpaRepository;
import com.safo.icos.modules.storage.api.StorageProvider;
import com.safo.icos.seeder.dto.UserSeedDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Component
@Profile({"dev", "seed"})
@Slf4j
public class UserSeeder extends AbstractJsonSeeder<UserSeedDto> {

  private final UserJpaRepository userRepository;
  private final UserProfileJpaRepository profileRepository;
  private final AuthCredentialsJpaRepository credentialsRepository;
  private final PasswordEncoder passwordEncoder;
  private final StorageProvider storageProvider;

  public UserSeeder(
          UserJpaRepository userRepository,
          UserProfileJpaRepository profileRepository,
          AuthCredentialsJpaRepository credentialsRepository,
          PasswordEncoder passwordEncoder,
          StorageProvider storageProvider) {
    super("seed/users.json", UserSeedDto[].class);
    this.userRepository = userRepository;
    this.profileRepository = profileRepository;
    this.credentialsRepository = credentialsRepository;
    this.passwordEncoder = passwordEncoder;
    this.storageProvider = storageProvider;
  }

  @Override
  public void seed() {
    List<UserSeedDto> dtos = loadDtos();
    if (dtos.isEmpty()) {
      log.warn("No users found in seed data file.");
      return;
    }

    int processedCount = 0;
    for (UserSeedDto dto : dtos) {
      UUID userId = parseUuid(String.valueOf(dto.getId()));

      String avatarPath = dto.getAvatarPath();
      if (avatarPath != null && !avatarPath.isBlank()) {
        boolean exists = false;
        try (var is = storageProvider.getFile(avatarPath)) {
          if (is != null) {
            exists = true;
          }
        } catch (Exception e) {
          log.warn("Avatar file not found in MinIO for user {}: {}", dto.getEmail(), avatarPath);
          avatarPath = null;
        }

        if (!exists) {
          log.warn("Avatar file not found in MinIO for user {}: {}", dto.getEmail(), avatarPath);
          avatarPath = null;
        }
      }

      LocalDate hireDate = parseDate(dto.getHireDate());
      LocalDate birthDate = parseDate(dto.getBirthDate());

      UserJpaEntity user = UserJpaEntity.builder()
              .id(userId)
              .email(dto.getEmail())
              .firstName(dto.getFirstName())
              .lastName(dto.getLastName())
              .status(dto.getStatus() != null ? dto.getStatus() : "ACTIVE")
              .createdBy(parseUuid(String.valueOf(dto.getCreatedBy())))
              .createdAt(parseDateTime(dto.getCreatedAt()))
              .updatedAt(parseDateTime(dto.getUpdatedAt()))
              .build();
      userRepository.save(user);

      UserProfileJpaEntity profile = UserProfileJpaEntity.builder()
              .userId(userId)
              .phoneNumber(dto.getPhoneNumber())
              .address(dto.getAddress())
              .hireDate(hireDate)
              .birthDate(birthDate)
              .avatarPath(avatarPath)
              .build();
      profileRepository.save(profile);

      String rawPassword = dto.getPassword() != null ? dto.getPassword() : "password";
      String hashedPassword = passwordEncoder.encode(rawPassword);
      AuthCredentialsJpaEntity credentials = AuthCredentialsJpaEntity.builder()
              .userId(userId)
              .passwordHash(hashedPassword)
              .build();
      credentialsRepository.save(credentials);

      processedCount++;
    }

    log.info("Successfully seeded {} users (along with their profiles and credentials).", processedCount);
  }

  @Override
  public int getOrder() {
    return 4;
  }
}