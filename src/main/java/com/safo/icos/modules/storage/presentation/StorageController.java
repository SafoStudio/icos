package com.safo.icos.modules.storage.presentation;

import com.safo.icos.modules.storage.application.FileService;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/storage")
@RequiredArgsConstructor
public class StorageController {

    private final FileService fileService;

    @PostMapping("/avatar/{userId}")
    public ResponseEntity<String> uploadAvatar(
            @PathVariable UUID userId,
            @RequestParam("file") MultipartFile file) throws Exception {
        String path = fileService.uploadAvatar(userId, file.getInputStream(), file.getOriginalFilename(), file.getContentType());
        return ResponseEntity.ok(path);
    }

    @GetMapping("/avatar/{userId}")
    public ResponseEntity<byte[]> getAvatar(@PathVariable UUID userId) {
        byte[] bytes = fileService.getAvatarBytes(userId);
        if (bytes == null || bytes.length == 0) {
            return ResponseEntity.notFound().build();
        }
        String contentType = "image/png";
        if (bytes.length >= 3 && (bytes[0] & 0xFF) == 0xFF && (bytes[1] & 0xFF) == 0xD8 && (bytes[2] & 0xFF) == 0xFF) {
            contentType = "image/jpeg";
        } else if (bytes.length >= 4 && bytes[0] == 'R' && bytes[1] == 'I' && bytes[2] == 'F' && bytes[3] == 'F') {
            contentType = "image/webp";
        }
        return ResponseEntity.ok()
                .header(org.springframework.http.HttpHeaders.CONTENT_TYPE, contentType)
                .body(bytes);
    }

    @DeleteMapping("/avatar/{userId}")
    public ResponseEntity<Void> deleteAvatar(@PathVariable UUID userId) {
        fileService.deleteAvatar(userId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/document/{userId}/{docType}")
    public ResponseEntity<String> uploadDocument(
            @PathVariable UUID userId,
            @PathVariable String docType,
            @RequestParam("file") MultipartFile file) throws Exception {
        String path = fileService.uploadDocument(userId, docType, file.getOriginalFilename(), file.getInputStream(), file.getContentType());
        return ResponseEntity.ok(path);
    }

    @DeleteMapping("/document/{userId}/{docType}/{filename}")
    public ResponseEntity<Void> deleteDocument(
            @PathVariable UUID userId,
            @PathVariable String docType,
            @PathVariable String filename) {
        fileService.deleteDocument(userId, docType, filename);
        return ResponseEntity.noContent().build();
    }
}
