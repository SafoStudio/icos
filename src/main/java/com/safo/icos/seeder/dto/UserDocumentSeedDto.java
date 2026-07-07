package com.safo.icos.seeder.dto;

import java.util.UUID;
import lombok.Data;

@Data
public class UserDocumentSeedDto {
  private UUID id;
  private UUID userId;
  private String type;
  private String filename;
  private String filePath;
  private String contentType;
  private Long fileSize;
  private String createdAt;
  private String updatedAt;
}
