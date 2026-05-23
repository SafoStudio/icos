package com.safo.icos.modules.storage.application;

import com.safo.icos.modules.storage.api.StorageProvider;
import com.safo.icos.modules.user.application.UserService;
import com.safo.icos.modules.user.application.dto.UserResponseDto;
import java.io.InputStream;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FileService {

    private final StorageProvider storageProvider;
    private final UserService userService;

    @SneakyThrows
    public String uploadAvatar(UUID userId, InputStream inputStream, String originalFilename, String contentType) {
        UserResponseDto user = userService.getUserById(userId);
        String path = storageProvider.getAvatarPath(user.getCompanyId(), user.getEmail(), originalFilename);
        
        storageProvider.uploadFile(path, inputStream, contentType);
        userService.updateAvatarPath(userId, path);
        
        return path;
    }

    public void deleteAvatar(UUID userId) {
        UserResponseDto user = userService.getUserById(userId);
        if (user.getProfile() != null && user.getProfile().getAvatarPath() != null) {
            storageProvider.deleteFile(user.getProfile().getAvatarPath());
            userService.updateAvatarPath(userId, null);
        }
    }

    @SneakyThrows
    public String uploadDocument(UUID userId, String docType, String filename, InputStream inputStream, String contentType) {
        UserResponseDto user = userService.getUserById(userId);
        String path = storageProvider.getDocumentPath(user.getCompanyId(), user.getEmail(), docType, filename);
        
        long size = inputStream.available(); // Note: for very large files this might be tricky, but works for docs
        storageProvider.uploadFile(path, inputStream, contentType);
        
        userService.saveDocumentMetadata(userId, docType, filename, path, contentType, size);
        return path;
    }

    public void deleteDocument(UUID userId, String docType, String filename) {
        UserResponseDto user = userService.getUserById(userId);
        String path = storageProvider.getDocumentPath(user.getCompanyId(), user.getEmail(), docType, filename);
        
        storageProvider.deleteFile(path);
        userService.deleteDocumentMetadata(userId, docType, filename);
    }
}
