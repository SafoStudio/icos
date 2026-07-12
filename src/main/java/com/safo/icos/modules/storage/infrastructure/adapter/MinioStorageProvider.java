package com.safo.icos.modules.storage.infrastructure.adapter;

import com.safo.icos.modules.storage.api.StorageProvider;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import java.io.InputStream;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MinioStorageProvider implements StorageProvider {

    private final MinioClient minioClient;

    @Value("${MINIO_BUCKET}")
    private String bucket;

    @Override
    @SneakyThrows
    public void uploadFile(String path, InputStream inputStream, String contentType) {
        minioClient.putObject(
                PutObjectArgs.builder()
                        .bucket(bucket)
                        .object(path)
                        .stream(inputStream, inputStream.available(), -1)
                        .contentType(contentType)
                        .build()
        );
    }

    @Override
    @SneakyThrows
    public void deleteFile(String path) {
        minioClient.removeObject(
                RemoveObjectArgs.builder()
                        .bucket(bucket)
                        .object(path)
                        .build()
        );
    }

    @Override
    public String getAvatarPath(UUID companyId, String email, String originalFilename) {
        String emailSlug = email.replace("@", "_").replace(".", "_");
        String extension = getExtension(originalFilename);
        return String.format("%s/%s/avatar/avatar%s", companyId, emailSlug, extension);
    }

    @Override
    public String getDocumentPath(UUID companyId, String email, String docType, String filename) {
        String emailSlug = email.replace("@", "_").replace(".", "_");
        return String.format("%s/%s/documents/%s/%s", companyId, emailSlug, docType.toLowerCase(), filename);
    }

    private String getExtension(String filename) {
        if (filename == null || !filename.contains(".")) return "";
        return filename.substring(filename.lastIndexOf("."));
    }
}
