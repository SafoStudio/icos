package com.safo.icos.modules.identity.user.application.dto;

import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DocumentResponseDto {
    private UUID id;
    private String type;
    private String filename;
    private String filePath;
    private String contentType;
    private Long fileSize;
    private LocalDateTime createdAt;
}
