package com.safo.icos.modules.storage.presentation;

import com.safo.icos.modules.identity.user.application.UserService;
import com.safo.icos.modules.identity.user.application.dto.DocumentResponseDto;
import com.safo.icos.modules.storage.application.FileService;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
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
    private final UserService userService;

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
                .header(HttpHeaders.CONTENT_TYPE, contentType)
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
        String filename = sanitizeFilename(file.getOriginalFilename());
        String path = fileService.uploadDocument(userId, docType, filename, file.getInputStream(), file.getContentType(), file.getSize());
        return ResponseEntity.ok(path);
    }

    @PostMapping("/document/{userId}")
    public ResponseEntity<String> uploadDocumentDefault(
            @PathVariable UUID userId,
            @RequestParam(value = "type", defaultValue = "OTHER") String docType,
            @RequestParam("file") MultipartFile file) throws Exception {
        String filename = sanitizeFilename(file.getOriginalFilename());
        String path = fileService.uploadDocument(userId, docType, filename, file.getInputStream(), file.getContentType(), file.getSize());
        return ResponseEntity.ok(path);
    }

    private String sanitizeFilename(String filename) {
        if (filename == null || filename.isBlank()) {
            return "document";
        }
        String clean = filename;
        if (clean.contains("\\")) {
            clean = clean.substring(clean.lastIndexOf("\\") + 1);
        }
        if (clean.contains("/")) {
            clean = clean.substring(clean.lastIndexOf("/") + 1);
        }
        return clean;
    }

    @GetMapping("/document/{userId}/{docType}/{filename}")
    public ResponseEntity<byte[]> getDocument(
            @PathVariable UUID userId,
            @PathVariable String docType,
            @PathVariable String filename) {
        byte[] bytes = fileService.getDocumentBytes(userId, docType, filename);
        if (bytes == null || bytes.length == 0) {
            return ResponseEntity.notFound().build();
        }
        String contentType = fileService.getDocumentContentType(userId, docType, filename);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, contentType)
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + filename + "\"")
                .body(bytes);
    }

    @GetMapping("/document/{userId}/id/{documentId}")
    public ResponseEntity<byte[]> getDocumentById(
            @PathVariable UUID userId,
            @PathVariable UUID documentId) {
        byte[] bytes = fileService.getDocumentBytesById(userId, documentId);
        if (bytes == null || bytes.length == 0) {
            return ResponseEntity.notFound().build();
        }
        String contentType = fileService.getDocumentContentTypeById(userId, documentId);
        String filename = fileService.getDocumentFilenameById(userId, documentId);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, contentType)
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + filename + "\"")
                .body(bytes);
    }

    @GetMapping("/document/{userId}/download/{documentId}")
    public ResponseEntity<byte[]> downloadDocumentById(
            @PathVariable UUID userId,
            @PathVariable UUID documentId) {
        byte[] bytes = fileService.getDocumentBytesById(userId, documentId);
        if (bytes == null || bytes.length == 0) {
            return ResponseEntity.notFound().build();
        }
        String contentType = fileService.getDocumentContentTypeById(userId, documentId);
        String filename = fileService.getDocumentFilenameById(userId, documentId);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, contentType)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .body(bytes);
    }

    @GetMapping("/documents/{userId}")
    public ResponseEntity<List<DocumentResponseDto>> getUserDocuments(
            @PathVariable UUID userId) {
        return ResponseEntity.ok(fileService.getUserDocuments(userId));
    }

    @DeleteMapping("/document/{userId}/{docType}/{filename}")
    public ResponseEntity<Void> deleteDocument(
            @PathVariable UUID userId,
            @PathVariable String docType,
            @PathVariable String filename) {
        fileService.deleteDocument(userId, docType, filename);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/document/{userId}/id/{documentId}")
    public ResponseEntity<Void> deleteDocumentById(
            @PathVariable UUID userId,
            @PathVariable UUID documentId) {
        fileService.deleteDocumentById(userId, documentId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/document/{userId}/{documentId}")
    public ResponseEntity<DocumentResponseDto> updateDocumentType(
            @PathVariable UUID userId,
            @PathVariable UUID documentId,
            @RequestParam("type") String type) {
        DocumentResponseDto updated = userService.updateDocumentMetadata(userId, documentId, type);
        if (updated == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(updated);
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(org.springframework.web.multipart.MaxUploadSizeExceededException.class)
    public ResponseEntity<String> handleMaxSizeException(org.springframework.web.multipart.MaxUploadSizeExceededException exc) {
        return ResponseEntity.status(org.springframework.http.HttpStatus.PAYLOAD_TOO_LARGE)
                .body("File size exceeds allowable limit (maximum 50MB)");
    }
}
