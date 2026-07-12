/**
 * File storage module.
 * Provides avatar and document upload/delete operations
 * backed by a pluggable {@link com.safo.icos.modules.storage.api.StorageProvider}
 * (e.g., MinIO, S3, local filesystem).
 *
 * <h3>Module layers</h3>
 * <ul>
 *   <li>{@code api} — {@code StorageProvider} contract.</li>
 *   <li>{@code application} — {@code FileService} orchestrating storage and metadata.</li>
 *   <li>{@code infrastructure} — {@code StorageProvider} implementation (e.g., MinIO adapter).</li>
 * </ul>
 *
 * @see com.safo.icos.modules.storage.api.StorageProvider
 * @see com.safo.icos.modules.storage.application.FileService
 */
package com.safo.icos.modules.storage;