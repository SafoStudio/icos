package com.safo.icos.seeder;

import com.safo.icos.modules.identity.user.infrastructure.repository.entity.UserDocumentJpaEntity;
import com.safo.icos.modules.identity.user.infrastructure.repository.jpa.UserDocumentJpaRepository;
import com.safo.icos.seeder.dto.UserDocumentSeedDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Profile({"dev", "seed"})
@Slf4j
public class UserDocumentSeeder extends AbstractJsonSeeder<UserDocumentSeedDto> {

  private final UserDocumentJpaRepository userDocumentRepository;

  public UserDocumentSeeder(UserDocumentJpaRepository userDocumentRepository) {
    super("seed/user_documents.json", UserDocumentSeedDto[].class);
    this.userDocumentRepository = userDocumentRepository;
  }

  @Override
  public void seed() {
    List<UserDocumentSeedDto> dtos = loadDtos();
    if (dtos.isEmpty()) {
      log.warn("No user documents found in seed data file.");
      return;
    }

    List<UserDocumentJpaEntity> entities = dtos.stream()
            .map(dto -> UserDocumentJpaEntity.builder()
                    .id(parseUuid(String.valueOf(dto.getId())))
                    .userId(parseUuid(String.valueOf(dto.getUserId())))
                    .type(dto.getType())
                    .filename(dto.getFilename())
                    .filePath(dto.getFilePath())
                    .contentType(dto.getContentType())
                    .fileSize(dto.getFileSize())
                    .createdAt(parseDateTime(dto.getCreatedAt()))
                    .updatedAt(parseDateTime(dto.getUpdatedAt()))
                    .build())
            .toList();

    userDocumentRepository.saveAll(entities);
    log.info("Successfully seeded {} user documents.", entities.size());
  }

  @Override
  public int getOrder() {
    return 6;
  }
}
