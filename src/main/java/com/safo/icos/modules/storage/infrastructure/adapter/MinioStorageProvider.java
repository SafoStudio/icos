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

    private void ensureBucketExists() {
        try {
            boolean found = minioClient.bucketExists(
                    io.minio.BucketExistsArgs.builder().bucket(bucket).build()
            );
            if (!found) {
                minioClient.makeBucket(
                        io.minio.MakeBucketArgs.builder().bucket(bucket).build()
                );
            }
        } catch (Exception e) {
            // Log or ignore if bucket exists
        }
    }

    @Override
    @SneakyThrows
    public void uploadFile(String path, InputStream inputStream, String contentType) {
        ensureBucketExists();
        long size = inputStream.available();
        if (size <= 0) {
            byte[] bytes = inputStream.readAllBytes();
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucket)
                            .object(path)
                            .stream(new java.io.ByteArrayInputStream(bytes), bytes.length, -1)
                            .contentType(contentType)
                            .build()
            );
        } else {
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucket)
                            .object(path)
                            .stream(inputStream, size, -1)
                            .contentType(contentType)
                            .build()
            );
        }
    }

    @Override
    @SneakyThrows
    public void deleteFile(String path) {
        try {
            minioClient.removeObject(
                    RemoveObjectArgs.builder()
                            .bucket(bucket)
                            .object(path)
                            .build()
            );
        } catch (Exception ignored) {
        }
    }

    @Override
    public String getAvatarPath(UUID companyId, String email, String originalFilename) {
        String comp = companyId != null ? companyId.toString() : "common";
        String emailSlug = email != null ? email.replace("@", "_").replace(".", "_") : "user";
        String extension = getExtension(originalFilename);
        return String.format("%s/%s/avatar/avatar%s", comp, emailSlug, extension);
    }

    @Override
    public String getDocumentPath(UUID companyId, String email, String docType, String filename) {
        String comp = companyId != null ? companyId.toString() : "common";
        String emailSlug = email != null ? email.replace("@", "_").replace(".", "_") : "user";
        String cleanType = docType != null ? docType.toLowerCase() : "other";
        return String.format("%s/%s/documents/%s/%s", comp, emailSlug, cleanType, filename);
    }

    @Override
    public InputStream getFile(String path) {
        try {
            ensureBucketExists();
            return minioClient.getObject(
                    io.minio.GetObjectArgs.builder()
                            .bucket(bucket)
                            .object(path)
                            .build()
            );
        } catch (Exception e) {
            return null;
        }
    }

    private String getExtension(String filename) {
        if (filename == null || !filename.contains(".")) return "";
        return filename.substring(filename.lastIndexOf("."));
    }
}
