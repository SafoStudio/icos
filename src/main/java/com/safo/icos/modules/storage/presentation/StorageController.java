package com.safo.icos.modules.storage.presentation;

import com.safo.icos.modules.storage.application.FileService;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
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
