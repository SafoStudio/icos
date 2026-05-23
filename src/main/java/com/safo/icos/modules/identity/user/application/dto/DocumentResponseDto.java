package com.safo.icos.modules.user.application.dto;

import java.time.LocalDateTime;
import java.util.UUID;
import lombok.*;

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
