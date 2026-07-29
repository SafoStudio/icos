package com.safo.icos.modules.storage.api;

import java.io.InputStream;
import java.util.UUID;

public interface StorageProvider {
    /**
     * Uploads a file to a specific path.
     * Path structure: companyId/emailSlug/folder/filename
     */
    void uploadFile(String path, InputStream inputStream, String contentType);

    /**
     * Deletes a file at the specific path.
     */
    void deleteFile(String path);

    /**
     * Generates a path for a user's avatar.
     * companyId/email_email/avatar/avatar.ext
     */
    String getAvatarPath(UUID companyId, String email, String originalFilename);

    /**
     * Generates a path for a user's document.
     * companyId/email_email/documents/type/filename
     */
    String getDocumentPath(UUID companyId, String email, String docType, String filename);

    /**
     * Retrieves a file stream by its path.
     */
    InputStream getFile(String path);
}
